/* Copyright Rupert Smith, 2005 to 2008, all rights reserved. */
package org.codehaus.mojo.simian;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import au.com.redhillconsulting.simian.AuditListener;
import au.com.redhillconsulting.simian.Options;
import au.com.redhillconsulting.simian.SourceFile;

/**
 * SimianAuditListener listens to the output of the simian checker and retains as {@link BlockInstance}s information
 * about all of the duplicate blocks that it finds. It provides this information as an {@link AuditReport}
 *
 * <p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities <th> Collaborations
 * <tr><td> Accept notification of all Simian audit events.
 * <tr><td> Record all duplicate blocks.
 * <tr><td> Maintain total duplicate counts per block, per line and per file.
 * <tr><td> Provide all repeated blocks grouped by block.
 * <tr><td> Record the execution time of a scan.
 * </table>
 *
 * @author Miguel Griffa
 * @author Rupert Smith
 */
public class SimianAuditListener implements AuditListener, AuditReport
{
    /** Holds the records of all duplicate code blocks found. */
    private List<BlockInstance> records = new ArrayList<BlockInstance>();

    /** Holds a listing of all of the source files scanned. */
    private List<SourceFile> processed = new ArrayList<SourceFile>();

    /** Holds for each source file, the total number of duplicate blocks found. */
    private Map<String, Integer> totalsByFilename = new HashMap<String, Integer>();

    /** Holds for each duplicate block, a summary of the block and a list of all occurences. */
    private Map<BlockSummary, List<BlockInstance>> recordsByBlock =
        new TreeMap<BlockSummary, List<BlockInstance>>(new BlockSummarySizeComparator());

    /** Holds the time at whcih the scan started. */
    private long startTimeMillis;

    /** Holds the time at which the scan completed. */
    private long endTimeMillis;

    /** Holds the size of the block in the block set currently being processed. */
    private int currentBlockSize;

    /** Holds a count to generate unique ids to identify source code blocks with. */
    private int currentBlockId = 1;

    /** Holds the count of the total number of duplicate lines found. */
    private int totalLines = 0;

    /** Holds the count of the total number of duplicate blocks found. */
    private int blockCount = 0;

    /** Holds the count of the total number of source code lines. */
    private int totalSouceLines;

    /**
     * Notified when the check begins with the set of active check options being used.
     *
     * @param options The check options being used.
     */
    public void startCheck(final Options options)
    {
        startTimeMillis = System.currentTimeMillis();
    }

    /**
     * Notified once a source file has completed its scan. Updates the total duplicate block count for the file.
     *
     * @param sourceFile The source file that has been completely scanned.
     */
    public void fileProcessed(final SourceFile sourceFile)
    {
        processed.add(sourceFile);
        totalSouceLines += sourceFile.getSignificantLineCount();
    }

    /**
     * Notified at the start of a set blocks of duplicate lines.
     *
     * @param lineCount The count of the number of source lines in the block.
     */
    public void startSet(final int lineCount)
    {
        currentBlockSize = lineCount;
    }

    /**
     * Called once for each block of duplicate lines within a set. Creates a new record for the block and updates
     * the total line count, total line count per file and total duplicate block count.
     *
     * @param sourcefile The file that contains the block.
     * @param startLine  The start line number within the file of the block.
     * @param endLine    The end line number within the file of the block.
     * @param subsumed   <tt>true</tt> if the block is contained within a larger duplicate block.
     */
    public void block(final SourceFile sourcefile, final int startLine, final int endLine, final boolean subsumed)
    {
        BlockInstance r = new BlockInstance(currentBlockId, currentBlockSize, sourcefile, startLine, endLine, subsumed);
        records.add(r);

        BlockSummary blockSummary = new BlockSummary(currentBlockId, currentBlockSize);
        List<BlockInstance> records = recordsByBlock.get(blockSummary);

        if (records == null)
        {
            records = new ArrayList<BlockInstance>();
            recordsByBlock.put(blockSummary, records);
        }

        records.add(r);

        // Update the running totals.
        addBlock(sourcefile.getFilename(), endLine - startLine);
        totalLines += (endLine - startLine);

        blockCount++;
    }

    /**
     * Notified at the end of a set of blocks of duplicate lines.
     */
    public void endSet()
    {
        synchronized (this)
        {
            currentBlockId++;
        }
    }

    /**
     * Notified when the check over all files completes.
     */
    public void endCheck()
    {
        endTimeMillis = System.currentTimeMillis();
    }

    /**
     * Provides the total ellapsed checking time in milliseconds.
     *
     * @return The total ellapsed checking time in milliseconds.
     */
    public long getProcessingTime()
    {
        return (endTimeMillis - startTimeMillis);
    }

    /**
     * Provides all of the duplicate block records.
     *
     * @return All of the duplicate block records.
     */
    public BlockSummary[] getRecords()
    {
        return records.toArray(new BlockInstance[records.size()]);
    }

    /**
     * Provides all of the duplicate block records by block id. That is, grouped by block so that all occurences of the
     * duplicate block are presented together.
     *
     * <p/>The implementation should use a SortedMap to present the blocks in order of size, the largest first.
     *
     * @return All of the duplicate block records by block id.
     */
    public Map<BlockSummary, List<BlockInstance>> getRecordsByBlock()
    {
        return recordsByBlock;
    }

    /**
     * For a given source file, provides a count of the total number of duplicate lines in the file.
     *
     * @param filename The name of the file to get the duplicate line count for.
     *
     * @return A count of the total number of duplicate lines in the file.
     */
    public int getTotalLinesForFilename(String filename)
    {
        if (totalsByFilename.containsKey(filename))
        {
            return totalsByFilename.get(filename);
        }

        return 0;
    }

    /**
     * Provides the total duplicate line count accross all files.
     *
     * @return The total duplicate line count accross all files.
     */
    public int getDuplicateLineCount()
    {
        return totalLines;
    }

    /**
     * Provides the total duplicate block count accross all files.
     *
     * @return The total duplicate block count accross all files.
     */
    public int getDuplicateBlockCount()
    {
        return blockCount;
    }

    /**
     * Provides the count of the total number of file scanned.
     *
     * @return The count of the total number of file scanned.
     */
    public int getTotalFileCount()
    {
        return processed.size();
    }

    /**
     * Provides the count of the total number of source lines accross all files.
     *
     * @return The count of the total number of source lines accross all files.
     */
    public int getSignificantLineCount()
    {
        return this.totalSouceLines;
    }

    /**
     * Provides the count of the total number of files with duplicates.
     *
     * @return The count of the total number of files with duplicates.
     */
    public int getDuplicateFileCount()
    {
        return totalsByFilename.size();
    }

    /**
     * Adds the count of duplicate lines to the specified file.
     *
     * @param filename  The file to add to the total count of.
     * @param lineCount The line count to add to the file.
     */
    private void addBlock(String filename, int lineCount)
    {
        if (!totalsByFilename.containsKey(filename))
        {
            totalsByFilename.put(filename, lineCount);
        }
        else
        {
            int val = totalsByFilename.get(filename);
            totalsByFilename.put(filename, val + lineCount);
        }
    }
}
