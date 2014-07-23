/* Copyright Rupert Smith, 2005 to 2008, all rights reserved. */
package org.codehaus.mojo.simian;

import java.util.List;
import java.util.Map;

/**
 * AuditReport provides the results of running a duplicates scan over a set of files.
 *
 * <p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities <th> Collaborations
 * <tr><td> Provide all repeated blocks grouped by block.
 * <tr><td> Provide all repeated blocks.
 * <tr><td> Provide duplicate line count per file.
 * <tr><td> Provide the total duplicate line count.
 * <tr><td> Provide the total duplicate block count.
 * <tr><td> Provide the total number of files scanned.
 * <tr><td> Provide the number of significant line scanned.
 * <tr><td> Provide the total count of files with duplicates.
 * <tr><td> Provide the processing time in milliseconds.
 * </table>
 *
 * @author Rupert Smith
 */
public interface AuditReport
{
    /**
     * Provides all of the duplicate block records by block id. That is, grouped by block so that all occurences of the
     * duplicate block are presented together.
     *
     * <p/>The implementation should use a SortedMap to present the blocks in order of size, the largest first.
     *
     * @return All of the duplicate block records by block id.
     */
    Map<BlockSummary, List<BlockInstance>> getRecordsByBlock();

    /**
     * Provides all of the duplicate block records.
     *
     * @return All of the duplicate block records.
     */
    BlockSummary[] getRecords();

    /**
     * For a given source file, provides a count of the total number of duplicate lines in the file.
     *
     * @param filename The name of the file to get the duplicate line count for.
     *
     * @return A count of the total number of duplicate lines in the file.
     */
    int getTotalLinesForFilename(String filename);

    /**
     * Provides the total duplicate line count accross all files.
     *
     * @return The total duplicate line count accross all files.
     */
    int getDuplicateLineCount();

    /**
     * Provides the total duplicate block count accross all files.
     *
     * @return The total duplicate block count accross all files.
     */
    int getDuplicateBlockCount();

    /**
     * Provides the count of the total number of file scanned.
     *
     * @return The count of the total number of file scanned.
     */
    int getTotalFileCount();

    /**
     * Provides the count of the total number of source lines accross all files.
     *
     * @return The count of the total number of source lines accross all files.
     */
    int getSignificantLineCount();

    /**
     * Provides the count of the total number of files with duplicates.
     *
     * @return The count of the total number of files with duplicates.
     */
    int getDuplicateFileCount();

    /**
     * Provides the total ellapsed checking time in milliseconds.
     *
     * @return The total ellapsed checking time in milliseconds.
     */
    long getProcessingTime();
}
