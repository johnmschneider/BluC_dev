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

import bluC.Result;
import bluC.parser.Parser;

/**
 * Contains utility classes for verifying if the operands of an equality
 *  comparison are valid to be used with the operator.
 *  
 * @author John Schneider
 */
public class ComparisonOperandsValidator
{
    private final Parser parser;
    
    public static enum IsLeftOperandValidErrCode
    {
        
    }
    
    public static class IsLeftOperandValidResult extends
        Result<IsLeftOperandValidErrCode>
    {
        public IsLeftOperandValidResult(IsLeftOperandValidErrCode errCode)
        {
            super(errCode);
        }
    }
    
    
    public ComparisonOperandsValidator(Parser parser)
    {
        this.parser = parser;
    }
    
    
    public IsLeftOperandValidResult isLeftOperandValid()
    {
        return null;
    }
}
