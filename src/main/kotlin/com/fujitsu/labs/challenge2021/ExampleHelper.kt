package com.fujitsu.labs.challenge2021

import org.apache.log4j.ConsoleAppender
import org.apache.log4j.Level
import org.apache.log4j.Logger
import org.apache.log4j.PatternLayout
import org.wikidata.wdtk.datamodel.interfaces.EntityDocumentProcessor
import org.wikidata.wdtk.dumpfiles.DumpContentType
import org.wikidata.wdtk.dumpfiles.DumpProcessingController
import org.wikidata.wdtk.dumpfiles.EntityTimerProcessor
import org.wikidata.wdtk.dumpfiles.EntityTimerProcessor.TimeoutException
import org.wikidata.wdtk.dumpfiles.MwDumpFile
import java.io.FileOutputStream
import java.io.IOException
import java.nio.file.FileAlreadyExistsException
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths


/**
 * Class for sharing code that is used in many examples. It contains several
 * static final members that can be modified to change the behaviour of example
 * programs, such as whether to use [ExampleHelpers.OFFLINE_MODE] or not.
 *
 * @author Markus Kroetzsch
 */
object ExampleHelpers {
    /**
     * If set to true, all example programs will run in offline mode. Only data
     * dumps that have been downloaded in previous runs will be used.
     */
    val OFFLINE_MODE = false

    /**
     * Defines which dumps will be downloaded and processed in all examples.
     */
    val DUMP_FILE_MODE = DumpProcessingMode.JSON

    /**
     * The directory where to place files created by the example applications.
     */
    val EXAMPLE_OUTPUT_DIRECTORY = "results"

    /**
     * Timeout to abort processing after a short while or 0 to disable timeout.
     * If set, then the processing will cleanly exit after about this many
     * seconds, as if the dump file would have ended there. This is useful for
     * testing (and in particular better than just aborting the program) since
     * it allows for final processing and proper closing to happen without
     * having to wait for a whole dump file to process.
     */
    val TIMEOUT_SEC = 0
    /**
     * Returns the name of the dump file that was last processed. This can be
     * used to name files generated from this dump. The result might be the
     * empty string if no file has been processed yet.
     */
    /**
     * Identifier of the dump file that was processed last. This can be used to
     * name files generated while processing a dump file.
     */
    var lastDumpFileName = ""
//        private set

    /**
     * Defines how messages should be logged. This method can be modified to
     * restrict the logging messages that are shown on the console or to change
     * their formatting. See the documentation of Log4J for details on how to do
     * this.
     */
    fun configureLogging() {
        // Create the appender that will write log messages to the console.
        val consoleAppender = ConsoleAppender()
        // Define the pattern of log messages.
        // Insert the string "%c{1}:%L" to also show class name and line.
        val pattern = "%d{yyyy-MM-dd HH:mm:ss} %-5p - %m%n"
        consoleAppender.setLayout(PatternLayout(pattern))
        // Change to Level.ERROR for fewer messages:
        consoleAppender.setThreshold(Level.INFO)
        consoleAppender.activateOptions()
        Logger.getRootLogger().addAppender(consoleAppender)
    }

    /**
     * Processes all entities in a Wikidata dump using the given entity
     * processor. By default, the most recent JSON dump will be used. In offline
     * mode, only the most recent previously downloaded file is considered.
     *
     * @param entityDocumentProcessor the object to use for processing entities in this dump
     */
    fun processEntitiesFromWikidataDump(
        entityDocumentProcessor: EntityDocumentProcessor?
    ) {

        // Controller object for processing dumps:
        val dumpProcessingController = DumpProcessingController(
            "wikidatawiki"
        )
        dumpProcessingController.setOfflineMode(OFFLINE_MODE)

        // // Optional: Use another download directory:
        // dumpProcessingController.setDownloadDirectory(System.getProperty("user.dir"));

        // Should we process historic revisions or only current ones?
        val onlyCurrentRevisions: Boolean
        when (DUMP_FILE_MODE) {
            DumpProcessingMode.ALL_REVS, DumpProcessingMode.ALL_REVS_WITH_DAILIES -> onlyCurrentRevisions = false
            DumpProcessingMode.CURRENT_REVS, DumpProcessingMode.CURRENT_REVS_WITH_DAILIES, DumpProcessingMode.JSON, DumpProcessingMode.JUST_ONE_DAILY_FOR_TEST -> onlyCurrentRevisions =
                true
            else -> onlyCurrentRevisions = true
        }

        // Subscribe to the most recent entity documents of type wikibase item:
        dumpProcessingController.registerEntityDocumentProcessor(
            entityDocumentProcessor, null, onlyCurrentRevisions
        )

        // Also add a timer that reports some basic progress information:
        val entityTimerProcessor = EntityTimerProcessor(
            TIMEOUT_SEC
        )
        dumpProcessingController.registerEntityDocumentProcessor(
            entityTimerProcessor, null, onlyCurrentRevisions
        )
        var dumpFile: MwDumpFile? = null
        try {
            // Start processing (may trigger downloads where needed):
            when (DUMP_FILE_MODE) {
                DumpProcessingMode.ALL_REVS, DumpProcessingMode.CURRENT_REVS -> dumpFile = dumpProcessingController
                    .getMostRecentDump(DumpContentType.FULL)
                DumpProcessingMode.ALL_REVS_WITH_DAILIES, DumpProcessingMode.CURRENT_REVS_WITH_DAILIES -> {
                    val fullDumpFile: MwDumpFile = dumpProcessingController
                        .getMostRecentDump(DumpContentType.FULL)
                    val incrDumpFile: MwDumpFile = dumpProcessingController
                        .getMostRecentDump(DumpContentType.DAILY)
                    lastDumpFileName = (fullDumpFile.getProjectName().toString() + "-"
                            + incrDumpFile.getDateStamp() + "."
                            + fullDumpFile.getDateStamp())
                    dumpProcessingController.processAllRecentRevisionDumps()
                }
                DumpProcessingMode.JSON -> dumpFile = dumpProcessingController
                    .getMostRecentDump(DumpContentType.JSON)
                DumpProcessingMode.JUST_ONE_DAILY_FOR_TEST -> dumpFile = dumpProcessingController
                    .getMostRecentDump(DumpContentType.DAILY)
                else -> throw RuntimeException(
                    "Unsupported dump processing type "
                            + DUMP_FILE_MODE
                )
            }
            if (dumpFile != null) {
                lastDumpFileName = (dumpFile.getProjectName().toString() + "-"
                        + dumpFile.getDateStamp())
                dumpProcessingController.processDump(dumpFile)
            }
        } catch (e: TimeoutException) {
            // The timer caused a time out. Continue and finish normally.
        }

        // Print final timer results:
        entityTimerProcessor.close()
    }

    /**
     * Opens a new FileOutputStream for a file of the given name in the example
     * output directory ([ExampleHelpers.EXAMPLE_OUTPUT_DIRECTORY]). Any
     * file of this name that exists already will be replaced. The caller is
     * responsible for eventually closing the stream.
     *
     * @param filename the name of the file to write to
     * @return FileOutputStream for the file
     * @throws IOException if the file or example output directory could not be created
     */
    @Throws(IOException::class)
    fun openExampleFileOuputStream(filename: String?): FileOutputStream {
        var directoryPath: Path
        if (("" == lastDumpFileName)) {
            directoryPath = Paths.get(EXAMPLE_OUTPUT_DIRECTORY)
        } else {
            directoryPath = Paths.get(EXAMPLE_OUTPUT_DIRECTORY)
            createDirectory(directoryPath)
            directoryPath = directoryPath.resolve(lastDumpFileName)
        }
        createDirectory(directoryPath)
        val filePath = directoryPath.resolve(filename)
        return FileOutputStream(filePath.toFile())
    }

    /**
     * Create a directory at the given path if it does not exist yet.
     *
     * @param path the path to the directory
     * @throws IOException if it was not possible to create a directory at the given
     * path
     */
    @Throws(IOException::class)
    private fun createDirectory(path: Path) {
        try {
            Files.createDirectory(path)
        } catch (e: FileAlreadyExistsException) {
            if (!Files.isDirectory(path)) {
                throw e
            }
        }
    }

    /**
     * Enum to say which dumps should be downloaded and processed. Used as
     * possible values of [ExampleHelpers.DUMP_FILE_MODE].
     */
    enum class DumpProcessingMode {
        JSON, CURRENT_REVS, ALL_REVS, CURRENT_REVS_WITH_DAILIES, ALL_REVS_WITH_DAILIES, JUST_ONE_DAILY_FOR_TEST
    }
}