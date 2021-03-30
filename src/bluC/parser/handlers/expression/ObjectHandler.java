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

package bluC.parser.handlers.expression;

import bluC.transpiler.Expression;
import bluC.transpiler.Token;
import bluC.parser.Parser;

/**
 *
 * @author John Schneider
 */
public class ObjectHandler
{
    private ExpressionHandler expressionHandler;
    private Parser parser;
    
    public ObjectHandler(Parser parser, ExpressionHandler expressionHandler)
    {
        this.expressionHandler = expressionHandler;
        this.parser = parser;
    }
    
    
    public Expression handleObjectAccess(Expression.Variable var)
    {
        Token operator = parser.peek();
        Expression right;
        
        parser.nextToken();
        right = expressionHandler.handleExpression();
         
        return new Expression.Binary(operator, var, right);
    }
}
