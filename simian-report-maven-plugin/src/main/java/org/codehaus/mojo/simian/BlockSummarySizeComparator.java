/* Copyright Rupert Smith, 2005 to 2008, all rights reserved. */
package org.codehaus.mojo.simian;

import java.util.Comparator;

/**
 * BlockSummarySizeComparator compares {@link BlockSummary}s by size. Blocks with a larger size are more significant
 * that ones with a smaller size, so the comparison works in the same direction as the size.
 *
 * <p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities <th> Collaborations
 * <tr><td> Compare block summaries by size.
 * </table>
 *
 * @author Rupert Smith
 */
public class BlockSummarySizeComparator implements Comparator<BlockSummary>
{
    /**
     * Compares two block summaries by size.
     *
     * @param summary1 The first summary to compare.
     * @param summary2 The second summary to compare.
     *
     * @return A negative integer, zero, or a positive integer as the first argument is less than, equal to, or greater
     *         than the second.
     */
    public int compare(BlockSummary summary1, BlockSummary summary2)
    {
        int size2 = summary2.getSize();
        int size1 = summary1.getSize();

        return (size2 > size1) ? 1
                               : ((size2 < size1) ? -1 : Integer.valueOf(summary2.getId()).compareTo(summary1.getId()));
    }
}
