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
import bluC.parser.Parser;
import bluC.transpiler.Statement;

/**
 *
 * @author John Schneider
 */
public class WhileHandler
{
    private final Parser            parser;
    private final StatementHandler  statementHandler;
    private final LoopHandlerUtils  loopUtils;
    private final String            nameOfLoopType = "while";
    
    public WhileHandler(Parser parser, StatementHandler statementHandler)
    {
        this.parser             = parser;
        this.statementHandler   = statementHandler;
        loopUtils               = new LoopHandlerUtils(parser);
        loopUtils.setNameOfLoopType(nameOfLoopType);
    }
    
    /**
     * Leaves parser on the end of the loop (either the closing brace "}" 
     *  or ";" for single-statement loops)
     */
    public Statement handleWhileOrHigher()
    {
        int startIndex = parser.getCurTokIndex();
        
        if (parser.peekMatches("while"))
        {
            return handleWhileLoop();
        }
        
        parser.setToken(startIndex);
        return statementHandler.handlePackage();
    }
    
    /**
     * Leaves parser on the end of the while loop (either the closing brace "}" 
     *  or ";" for single-statement loops)
     */
    private Statement handleWhileLoop()
    {
        // consume "while" token
        parser.nextToken();
        
        if (parser.peekMatches("("))
        {
            return handleValidConditionStart();
        }
        else
        {
            return handleMalformedConditionStart();
        }
    }
    
    private Statement handleMalformedConditionStart()
    {
        Logger.err(parser.peek(), "Expected \"(\" to open condition for " +
            "while loop");
        loopUtils.synchronizeParserFromBadCondition();
    }
    
}
