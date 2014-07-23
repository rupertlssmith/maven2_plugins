/* Copyright Rupert Smith, 2005 to 2008, all rights reserved. */
package org.codehaus.mojo.simian;

import au.com.redhillconsulting.simian.Option;
import au.com.redhillconsulting.simian.Options;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.project.MavenProject;

/**
 * SimianMojoBase provides a base class for implementing mojo's that run Simian. Its purpose is to expose all
 * of Simians configurable options and to avoid the need to repeat these options amongst mojos that use Simian.
 *
 * <p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities <th> Collaborations
 * <tr><td> Convert the options set on this mojo into options that Simian understands. <td> {@link Options}.
 * </table>
 *
 * @author Rupert Smith
 */
public abstract class SimianMojoBase extends AbstractMojo
{
    /**
     * Matches will contain at least the specified number of lines.
     *
     * @parameter  default-value="8"
     * @optional
     */
    protected int threshold = 8;

    /**
     * MyVariable and myvariablewould both match.
     *
     * @parameter
     * @optional
     */
    protected boolean ignoreStrings;

    /**
     * Completely ignores all identfiers.
     *
     * @parameter
     * @optional
     */
    protected boolean ignoreIdentifiers;

    /**
     * Completely ignores variable names (field, parameter and local). Eg. int foo = 1; and int bar = 1 would both match.
     *
     * @parameter
     * @optional
     */
    protected boolean ignoreVariableNames;

    /**
     * Example: int x = 1; and int x = 576; would both match.
     *
     * @parameter
     * @optional
     */
    protected boolean ignoreNumbers;

    /**
     * Example: 'A', "one" and 27.8 would all match.
     *
     * @parameter
     * @optional
     */
    protected boolean ignoreLiterals;

    /**
     * Example: public, protected, static, etc.
     *
     * @parameter
     * @optional
     */
    protected boolean ignoreModifiers;

    /**
     * @parameter expression="${project}"
     * @required
     * @readonly
     */
    protected MavenProject project;

    /**
     * Ensures that expressions inside curly braces that are split across multiple physical lines are considered as one.
     *
     * @parameter
     * @optional
     */
    private boolean balanceCurlyBraces;

    /**
     * Ensures that expressions inside parenthesis that are split across multiple physical lines are considered as one.
     *
     * @parameter
     * @optional
     */
    private boolean balanceParentheses;

    /**
     * Ensures that expressions inside square brackets that are split across multiple physical lines are considered as
     * one. Defaults to false.
     *
     * @parameter
     * @optional
     */
    private boolean balanceSquareBrackets;

    /**
     * Curly braces are ignored.
     *
     * @parameter
     * @optional
     */
    private boolean ignoreCurlyBraces;

    /**
     * "Hello, World" and "HELLO, WORLD" would both match.
     *
     * @parameter
     * @optional
     */
    private boolean ignoreStringCase;

    /**
     * 'A' and 'a'would both match.
     *
     * @parameter
     * @optional
     */
    private boolean ignoreCharacterCase;

    /**
     * BufferedReader, StringReader and Reader would all match.
     *
     * @parameter
     * @optional
     */
    private boolean ignoreSubtypeNames;

    /**
     * 'A' and 'Z'would both match.
     *
     * @parameter
     * @optional
     */
    private boolean ignoreCharacters;

    /**
     * Matches identifiers irrespective of case. Eg. MyVariableName and myvariablename would both match.
     *
     * @parameter
     * @optional
     */
    private boolean ignoreIdentifierCase;

    /**
     * Converts the options set on this Mojo into a set of Options as accepted by Simian.
     *
     * @return A set of checking Options as accepted by Simian.
     */
    protected Options getOptions()
    {
        final Options options = new Options();

        options.setThreshold(threshold);
        options.setOption(Option.BALANCE_CURLY_BRACES, balanceCurlyBraces);
        options.setOption(Option.BALANCE_PARENTHESES, balanceParentheses);
        options.setOption(Option.BALANCE_SQUARE_BRACKETS, balanceSquareBrackets);
        options.setOption(Option.IGNORE_STRINGS, ignoreStrings);
        options.setOption(Option.IGNORE_IDENTIFIERS, ignoreIdentifiers);
        options.setOption(Option.IGNORE_NUMBERS, ignoreNumbers);
        options.setOption(Option.IGNORE_VARIABLE_NAMES, ignoreVariableNames);
        options.setOption(Option.IGNORE_LITERALS, ignoreLiterals);
        options.setOption(Option.IGNORE_MODIFIERS, ignoreModifiers);
        options.setOption(Option.IGNORE_CURLY_BRACES, ignoreCurlyBraces);
        options.setOption(Option.IGNORE_STRING_CASE, ignoreStringCase);
        options.setOption(Option.IGNORE_CHARACTER_CASE, ignoreCharacterCase);
        options.setOption(Option.IGNORE_SUBTYPE_NAMES, ignoreSubtypeNames);
        options.setOption(Option.IGNORE_CHARACTERS, ignoreCharacters);
        options.setOption(Option.IGNORE_IDENTIFIER_CASE, ignoreIdentifierCase);

        return options;
    }
}
