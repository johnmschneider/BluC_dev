/*
 * Copyright 2021 John Schneider.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package bluC.parser.handlers.statement;

import bluC.Logger;
import bluC.ResultType;
import bluC.builders.TokenBuilder;
import bluC.parser.Parser;
import bluC.transpiler.Token;

/**
 *
 * @author John Schneider
 */
public class LoopHandlerUtils
{
    private final Parser parser;
    private String       nameOfLoopType;
    
    private enum GotoStartErrCodes
    {
        NO_OPENING_PAREN,
        MALFORMED_SINGLE_STATEMENT_LOOP,
        MALFORMED_MULTI_STATEMENT_LOOP;
    }
    
    private static class GotoStartResults extends 
        ResultType<Token, GotoStartErrCodes>
    {
        public GotoStartResults(GotoStartErrCodes errCode)
        {
            super(errCode);
        }
    }
    
    public LoopHandlerUtils(Parser parser)
    {
        this.parser = parser;
    }

    
    public String getNameOfLoopType()
    {
        return nameOfLoopType;
    }

    public void setNameOfLoopType(String nameOfLoopType)
    {
        this.nameOfLoopType = nameOfLoopType;
    }
    
    /**
     * Synchronizes the parser, assuming that the condition section
     *  of the loop was malformed
     */
    public void synchronizeParserFromBadCondition()
    {
        Token   errorLocation;
        GotoStartResults
                findBlockStartResult;
        int     startIndex;
        
        startIndex              = parser.getCurTokIndex();
        errorLocation           = parser.getCurToken();
        findBlockStartResult    = gotoBlockStart();
        
        if (findBlockStartResult.getWasSuccessful())
        {
            handleMalformedStartCondition(
                startIndex, errorLocation, findBlockStartResult);
        }
        else
        {
            gotoBlockEnd(startIndex, errorLocation);
        }
    }
    
    private void handleMalformedStartCondition(
        int startIndex, Token errorLocation, GotoStartResults blockStartResult)
    {
        GotoStartErrCodes errCode        = blockStartResult.getErrCode();
        
        switch (errCode)
        {
            case NO_OPENING_PAREN:
                Logger.err(errorLocation, 
                    "Malformed " + nameOfLoopType + 
                    "-loop, expected \"(\" to open the conditional" + 
                    "section of the loop");
                // try to synchronize parser by properly starting the condition
                injectFakeConditionOpenParen(startIndex, errorLocation);
                
                // try rehandling this again, to see if it's also missing an
                //  ending parenthesis
                handleMalformedStartCondition(
                    startIndex, errorLocation, gotoBlockStart());
                
            case MALFORMED_SINGLE_STATEMENT_LOOP:
                Logger.err(errorLocation, 
                    "Malformed single-statement " + nameOfLoopType + 
                    "-loop, expected \")\" to close the conditional " + 
                    "section of the loop");
                /**
                 * try to synchronize parser by properly ending the condition
                 * 
                 *  (the only way to get this errCode is to be missing a ")",
                 *      and have no open brace "{" before the end of the next
                 *      statement, which we then try to synchronize as a
                 *      single-statement loop)
                 */
                injectFakeConditionCloseParen(startIndex, errorLocation);
                
                break;
            case MALFORMED_MULTI_STATEMENT_LOOP:
                Logger.err(errorLocation, 
                    "Malformed multi-statement " + nameOfLoopType + 
                    "-loop, expected \"}\" to close the opening brace of " +
                    "the loop");
                /**
                 * try to synchronize parser by properly ending the condition
                 * 
                 *  (the only way to get this errCode is to be missing a ")",
                 *      AND to have an opening brace "{" before the end of the
                 *      next statement, which we then try to synchronize as a
                 *      multi-statement loop)
                 */
                injectFakeConditionCloseParen(startIndex, errorLocation);
                
                break;
            default:
                /**
                 * I don't see any way for this branch to be reached unless 
                 *  the result enum was extended without also updating this
                 *  function.
                 * 
                 * It's only here because java complains otherwise.
                 */
                Logger.err(errorLocation,
                   "(FATAL error in compiler): encountered an unexpected " +
                    "way that a loop was malformed, one that the " +
                    "compiler didn't anticipate");
                break;
        }
        
        gotoBlockEnd(startIndex, errorLocation);
    }
    
    /**
     * Injects a compiler-generated "(" token and sets the current token index
     *  to the injected token.
     */
    private void injectFakeConditionOpenParen(
        int startIndex, Token errorLocation)
    {
        TokenBuilder    parenBuilder = new TokenBuilder();
        Token           openParen = parenBuilder.
            setFileName             (errorLocation.getFilepath()). 
            setLineIndex            (errorLocation.getLineIndex()).
            setTextContent          ("(").
            setWasEmittedByCompiler (true).
            build();
        
        // the actual startIndex will be the loop keyword token
        parser.addToken(openParen, startIndex + 1);
        parser.setToken(startIndex + 1);
    }
    
    /**
     * Injects a compiler-generated ")" token and sets the current token index
     *  to the injected token.
     */
    private void injectFakeConditionCloseParen(
        int startIndex, Token errorLocation)
    {
        TokenBuilder    parenBuilder = new TokenBuilder();
        Token           openParen = parenBuilder.
            setFileName             (errorLocation.getFilepath()). 
            setLineIndex            (errorLocation.getLineIndex()).
            setTextContent          (")").
            setWasEmittedByCompiler (true).
            build();
        
        // startIndex + 1 will be opening paren "("
        //
        //  (there's no easy way to guess what the actual condition might be,
        //  and at any rate it's erroneous, so just leave the condition blank).
        parser.addToken(openParen, startIndex + 2);
        parser.setToken(startIndex + 2);
    }
    
    /**
     * Leaves the parser on the opening token of the loop block (either 
     *  ")" for a single-statement loop or "{" for a multi-statement loop),
     *  or returns a failed ResultType if the conditional/opening statement of
     *  this loop was malformed (missing a ")").
     */
    private GotoStartResults gotoBlockStart()
    {
        GotoStartResults result;
        
        if (parser.peekMatches("("))
        {
            result = keepGoingToBlockStart();
        }
        else
        {
            result = new GotoStartResults(GotoStartErrCodes.NO_OPENING_PAREN);
        }
        
        return result;
    }
    
    /**
     * Helper method for gotoBlockStart. Shouldn't be called directly.
     */
    private GotoStartResults keepGoingToBlockStart()
    {
        GotoStartResults 
                result;
        boolean foundClosingParen;
        
        result              = new GotoStartResults(
            GotoStartErrCodes.MALFORMED_SINGLE_STATEMENT_LOOP);
        foundClosingParen   = false;
        
        while (!parser.atEOF())
        {
            if (parser.peekMatches(")"))
            {
                parser.nextToken();
                foundClosingParen = true;
                break;
            }
        }
        
        if (foundClosingParen)
        {
            //check if it's a multi-statement loop
            if (parser.peekMatches("{"))
            {
                result = new GotoStartResults(
                    GotoStartErrCodes.MALFORMED_MULTI_STATEMENT_LOOP);
            }
        }
        
        return result;
    }
    
    
    private void gotoBlockEnd(int startIndex, Token errorLocation)
    {
        boolean didLoopEnd;
        boolean isLoopMultiStatement = false;
        
        if (parser.curTextMatches(")"))
        {
            didLoopEnd = parser.gotoEndOfStatement();
        }
        else if (parser.curTextMatches("{"))
        {
            didLoopEnd              = parser.gotoEndOfBlock();
            isLoopMultiStatement    = true;
        }
        else
        {
            Logger.err(parser.getCurToken(), "(FATAL error in compiler): " +
                "could not synchronize parser on this \"" + nameOfLoopType +
                "\"loop with a malformed starting condition; unexpected " +
                "result from findBlockStart");

            /** 
             * try to synchronize the parser. I suppose synchronizing on a
             *  statement will do? This parser state shouldn't be possible.
             */
            didLoopEnd = parser.gotoEndOfStatement();
        }
        
        if (!didLoopEnd)
        {
            handleNoLoopEnding(startIndex, errorLocation, isLoopMultiStatement);
        }
    }
    
    private void handleNoLoopEnding(
        int startIndex, Token errorLocation, boolean isLoopMultiStatement)
    {
        if (!isLoopMultiStatement)
        {
            Logger.err(errorLocation,
                "Expected \";\" to end single-statement " + nameOfLoopType +
                    "-loop");
        }
        else
        {
            Logger.err(errorLocation,
                "Expected \"}\" to end multi-statement " + nameOfLoopType +
                    "-loop");
        }
        
        /**
         * startIndex + 2 should be the closing parenthesis of the conditional
         *  ")", and is the last known valid token, because the synchronization
         *  code makes sure of this. After that, the parser has no idea what 
         *  this stream of tokens might be, so set the index to this token and
         *  try to reinterpret the AST after this location.
         */
        parser.setToken(startIndex + 2);
    }
}
