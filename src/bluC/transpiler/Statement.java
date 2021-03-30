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

package bluC.transpiler;

import bluC.Utils;
import java.util.ArrayList;
import bluC.transpiler.Statement.VarDeclaration.Sign;
import bluC.transpiler.Statement.VarDeclaration.SimplifiedType;
import bluC.parser.Parser;
import bluC.parser.handlers.expression.ExpressionHandler;
import java.util.Objects;

/**
 * @author John Schneider
 */
public abstract class Statement
{
    public static long NO_STARTING_LINE_INDEX = -1;
    public static long NO_ENDING_LINE_INDEX   = -1;
    
    private long startingLineIndex  = NO_STARTING_LINE_INDEX;
    private long endingLineIndex    = NO_ENDING_LINE_INDEX;
    
    public static interface Visitor<T>
    {
        //blocks
        T visitBlock        (Statement.Block statement);
        
        T visitFunction     (Statement.Function statement);
        T visitMethod       (Statement.Method statement);
        T visitParameterList(Statement.ParameterList statement);
                    
        T visitIf           (Statement.If statement);
        T visitClassDef     (Statement.ClassDef statement);
        T visitStructDef    (Statement.StructDef statement);
        T visitWhile        (Statement.While statement);
        
        //misc
        T visitReturn               (Statement.Return statement);
        T visitExpressionStatement  (Statement.ExpressionStatement statement);
        T visitPackage              (Statement.Package statement);
        
        //vars
        T visitVarDeclaration       (Statement.VarDeclaration statement);
    }
    
    public static class Block extends Statement
    {
        private ArrayList<Statement> body;
        
        public Block(long startingLineIndex)
        {
            super(startingLineIndex);
            this.body = new ArrayList<>();
        }
        
        @Override
        <T> T accept(Visitor<T> visitor)
        {
            return visitor.visitBlock(this);
        }
        
        public final <T> T acceptBlock(Visitor<T> visitor)
        {
            return visitor.visitBlock(this);
        }
        
        public ArrayList<Statement> getBody()
        {
            return body;
        }
        
        public void addStatement(Statement statement)
        {
            body.add(statement);
        }
        
        @Override
        public boolean needsSemicolon()
        {
            return false;
        }
        
        public boolean needsExtraSpace()
        {
            return true;
        }

        @Override
        public int hashCode()
        {
            int hash = 3;
            hash = 79 * hash + Objects.hashCode(this.body);
            hash = 79 * hash + (int) (this.getStartingLineIndex() ^
                (this.getStartingLineIndex() >>> 32));
            hash = 79 * hash + (int) (this.getEndingLineIndex() ^ 
                (this.getEndingLineIndex() >>> 32));
            return hash;
        }

        @Override
        public boolean equals(Object obj)
        {
            if (this == obj)
            {
                return true;
            }
            if (obj == null)
            {
                return false;
            }
            if (getClass() != obj.getClass())
            {
                return false;
            }
            if (!super.equals(obj))
            {
                return false;
            }
            
            final Block other = (Block) obj;
            if (!Objects.equals(this.body, other.body))
            {
                return false;
            }
            return true;
        }
        
        
    }
    
    public static class Function extends Block
    {
        private Statement.VarDeclaration
                        returnType;
        private Statement.ParameterList
                        parameterList;
        private Token   functionName;
        
        public Function(Statement.VarDeclaration returnType, Token functionName,
            long startingLineIndex)
        {
            super(startingLineIndex);
            
            this.returnType = returnType;
            this.functionName = functionName;
        }
        
        public Statement.VarDeclaration getReturnType()
        {
            return returnType;
        }
        
        public boolean hasValidName()
        {
            return functionName.isValidName(); 
        }
        
        public Token getNameToken()
        {
            return functionName;
        }
        
        public String getNameText()
        {
            return functionName.getTextContent();
        }
        
        public void setName(Token newName)
        {
            functionName = newName;
        }
        
        public void setParameters(ParameterList parameters)
        {
            parameterList = parameters;
        }
        
        public Statement.ParameterList getParameters()
        {
            return parameterList;
        }

        @Override
        public int hashCode()
        {
            int hash = 3;
            hash = 37 * hash + Objects.hashCode(this.returnType);
            hash = 37 * hash + Objects.hashCode(this.parameterList);
            hash = 37 * hash + Objects.hashCode(this.functionName);
            hash = 37 * hash + (int) (this.getStartingLineIndex() ^ 
                (this.getStartingLineIndex() >>> 32));
            hash = 37 * hash + (int) (this.getEndingLineIndex() ^ 
                (this.getEndingLineIndex() >>> 32));
            return hash;
        }

        @Override
        public boolean equals(Object obj)
        {
            if (this == obj)
            {
                return true;
            }
            if (obj == null)
            {
                return false;
            }
            if (getClass() != obj.getClass())
            {
                return false;
            }
            if (!super.equals(obj))
            {
                return false;
            }
            
            final Function other = (Function) obj;
            if (!Objects.equals(this.returnType, other.returnType))
            {
                return false;
            }
            if (!Objects.equals(this.parameterList, other.parameterList))
            {
                return false;
            }
            if (!Objects.equals(this.functionName, other.functionName))
            {
                return false;
            }
            return true;
        }
        
        
        @Override
        <T> T accept(Visitor<T> visitor)
        {
            return visitor.visitFunction(this);
        }
    }
    
    public static class ParameterList extends Statement
    {
        private final ArrayList<Statement.VarDeclaration> parameters;
        
        public ParameterList(long startingLineIndex)
        {
            super(startingLineIndex);
            this.parameters = new ArrayList<>();
        }
        
        public void addParameter(Statement.VarDeclaration param)
        {
            parameters.add(param);
        }
        
        public ArrayList<Statement.VarDeclaration> getParameters()
        {
            return parameters;
        }

        @Override
        public int hashCode()
        {
            int hash = 3;
            hash = 13 * hash + Objects.hashCode(this.parameters);
            hash = 13 * hash + (int) (this.getStartingLineIndex() ^ 
                (this.getStartingLineIndex() >>> 32));
            hash = 13 * hash + (int) (this.getEndingLineIndex() ^ 
                (this.getEndingLineIndex() >>> 32));
            return hash;
        }

        @Override
        public boolean equals(Object obj)
        {
            if (this == obj)
            {
                return true;
            }
            if (obj == null)
            {
                return false;
            }
            if (getClass() != obj.getClass())
            {
                return false;
            }
            if (!super.equals(obj))
            {
                return false;
            }
            
            final ParameterList other = (ParameterList) obj;
            if (!Objects.equals(this.parameters, other.parameters))
            {
                return false;
            }
            return true;
        }
        
        @Override
        <T> T accept(Visitor<T> visitor)
        {
            return visitor.visitParameterList(this);
        }
    }
    
    public static class Method extends Function
    {
        private final Statement.ClassDef class_;
        private final String mangledName;
        private final Parser parser;
            
        public Method(Statement.ClassDef class_, VarDeclaration returnVar,
            Token methodName, String mangledName, Parser parser,
            long startingLineIndex)
        {
            super(returnVar, methodName, startingLineIndex);
            this.class_         = class_;
            this.mangledName    = mangledName;
            this.parser         = parser;
        }
        
        @Override
        public void setParameters(ParameterList parameters)
        {
            long                                startingLineIndex;
            ParameterList                       listWithThis;
            ArrayList<Statement.VarDeclaration> rawParameters;
            
            startingLineIndex    = parameters.getStartingLineIndex();
            listWithThis    = new ParameterList(startingLineIndex);
            rawParameters   = parameters.getParameters();
            
            // to determine what file and line the "this" is on
            Token tokenBeforeTheThisKeyword; 
            
            if (rawParameters.isEmpty())
            {
                // the "(" token
                tokenBeforeTheThisKeyword = parser.getCurToken();
            }
            else
            {
                tokenBeforeTheThisKeyword = rawParameters.get(0).getName();
            }
            
            VarDeclaration this_ = new VarDeclaration(Sign.UNSPECIFIED, 
                SimplifiedType.CLASS, 1,
                
                new Token(
                   new TokenInfo("this", false),
                        
                   new TokenFileInfo(tokenBeforeTheThisKeyword.getFilepath(), 
                       tokenBeforeTheThisKeyword.getLineIndex())
                ), 
                    
                null, null, tokenBeforeTheThisKeyword.getLineIndex());
            
            this_.setClassID(class_.getClassID());
            parser.getCurrentScope().addVariableToScope(this_);
                    
            listWithThis.addParameter(this_);
            
            for (VarDeclaration parameter : parameters.getParameters())
            {
                listWithThis.addParameter(parameter);
            }
            
            super.setParameters(listWithThis);
        }
        
        public Statement.ClassDef getClass_()
        {
            return class_;
        }
        
        public String getMangledName()
        {
            return mangledName;
        }

        @Override
        public int hashCode()
        {
            int hash = 7;
            hash = 53 * hash + Objects.hashCode(this.class_);
            hash = 53 * hash + Objects.hashCode(this.mangledName);
            hash = 53 * hash + Objects.hashCode(this.parser);
            hash = 53 * hash + Objects.hashCode(this.getReturnType());
            hash = 53 * hash + Objects.hashCode(this.getParameters());
            hash = 53 * hash + Objects.hashCode(this.getNameToken());
            hash = 53 * hash + (int) (this.getStartingLineIndex() ^ 
                (this.getStartingLineIndex() >>> 32));
            hash = 53 * hash + (int) (this.getEndingLineIndex() ^ 
                (this.getEndingLineIndex() >>> 32));
            hash = 53 * hash + Objects.hashCode(this.getBody());
            return hash;
        }

        @Override
        public boolean equals(Object obj)
        {
            if (this == obj)
            {
                return true;
            }
            if (obj == null)
            {
                return false;
            }
            if (getClass() != obj.getClass())
            {
                return false;
            }
            if (!super.equals(obj))
            {
                return false;
            }
            
            final Method other = (Method) obj;
            if (!Objects.equals(this.mangledName, other.mangledName))
            {
                return false;
            }
            if (!Objects.equals(this.class_, other.class_))
            {
                return false;
            }
            if (!Objects.equals(this.parser, other.parser))
            {
                return false;
            }
            return true;
        }
        
        
        @Override
        <T> T accept(Visitor<T> visitor)
        {
            return visitor.visitMethod(this);
        }
    }
    
    public static class If extends Block
    {
        public static class ElseIf extends Block
        {
            private Expression condition;
            
            public ElseIf(Expression condition, long startingLineIndex)
            {
                super(startingLineIndex);
                this.condition = condition;
            }
            
            public Expression getCondition()
            {
                return condition;
            }
            
            @Override
            public boolean needsExtraSpace()
            {
                return false;
            }

            @Override
            public int hashCode()
            {
                int hash = 5;
                hash = 59 * hash + Objects.hashCode(this.condition);
                hash = 59 * hash + (int) (this.getStartingLineIndex() ^ 
                    (this.getStartingLineIndex() >>> 32));
                hash = 59 * hash + (int) (this.getEndingLineIndex() ^ 
                    (this.getEndingLineIndex() >>> 32));
                hash = 59 * hash + Objects.hashCode(this.getBody());
                return hash;
            }

            @Override
            public boolean equals(Object obj)
            {
                if (this == obj)
                {
                    return true;
                }
                if (obj == null)
                {
                    return false;
                }
                if (getClass() != obj.getClass())
                {
                    return false;
                }
                if (!super.equals(obj))
                {
                    return false;
                }
                
                final ElseIf other = (ElseIf) obj;
                if (!Objects.equals(this.condition, other.condition))
                {
                    return false;
                }
                return true;
            }
        }
        
        public static class Else extends Block
        {
            public Else(long startingLineIndex)
            {
                super(startingLineIndex);
            }
            
            @Override
            public boolean needsExtraSpace()
            {
                // the visitIf itself adds extra whitespace
                return false;
            }
            
            //.equals and .hashCode from Block should still work for this class
        }
        
        private Expression condition;
        private ArrayList<ElseIf> elseIfs;
        private Else else_;
        
        public If(Expression condition, long startingLineIndex)
        {
            super(startingLineIndex);
            this.condition = condition;
            elseIfs = new ArrayList<>();
            else_ = null;
        }
        
        public Expression getCondition()
        {
            return condition;
        }
        
        public void addElseIf(ElseIf elseIf)
        {
            elseIfs.add(elseIf);
        }
        
        public ArrayList<ElseIf> getElseIfs()
        {
            return elseIfs;
        }
        
        public void setElse(Else else_)
        {
            this.else_ = else_;
        }
        
        public Statement.Block getElse()
        {
            return else_;
        }

        @Override
        public int hashCode()
        {
            int hash = 3;
            hash = 53 * hash + (int) (this.getStartingLineIndex() ^ 
                (this.getStartingLineIndex() >>> 32));
            hash = 53 * hash + (int) (this.getEndingLineIndex() ^ 
                (this.getEndingLineIndex() >>> 32));
            hash = 53 * hash + Objects.hashCode(this.getBody());
            hash = 53 * hash + Objects.hashCode(this.condition);
            hash = 53 * hash + Objects.hashCode(this.elseIfs);
            hash = 53 * hash + Objects.hashCode(this.else_);
            return hash;
        }

        @Override
        public boolean equals(Object obj)
        {
            if (this == obj)
            {
                return true;
            }
            if (obj == null)
            {
                return false;
            }
            if (getClass() != obj.getClass())
            {
                return false;
            }
            if (!super.equals(obj))
            {
                return false;
            }
            
            final If other = (If) obj;
            if (!Objects.equals(this.condition, other.condition))
            {
                return false;
            }
            if (!Objects.equals(this.elseIfs, other.elseIfs))
            {
                return false;
            }
            if (!Objects.equals(this.else_, other.else_))
            {
                return false;
            }
            return true;
        }
        
        @Override
        <T> T accept(Visitor<T> visitor)
        {
            return visitor.visitIf(this);
        }
        
        @Override
        public boolean needsExtraSpace()
        {
            // the visitIf itself adds extra whitespace
            return false;
        }
    }
    
    public static class ClassDef extends Block
    {
        private Token className;
        private Token baseClass;
        private long classID;
        private static long nextClassID = Long.MIN_VALUE;
        public static final ClassDef objectBaseClass = new ClassDef("Object", 
            "bluc.lang", Statement.NO_STARTING_LINE_INDEX);
        
        /**
         * Helper constructor for other constructors.
         */
        private ClassDef(long startingLineIndex) 
        {
            super(startingLineIndex);
            
            this.baseClass = null;
            this.classID = nextClassID;
            
            nextClassID++;
        }
        
        private ClassDef(String className, String package_, long startingLineIndex)
        {
            this(startingLineIndex);
            
            this.className = new Token(
                new TokenInfo(className, true),
                    
                new TokenFileInfo("Statement.java", -1),
                package_);
        }
        
        public ClassDef(Token className, long startingLineIndex) 
        {
            this(startingLineIndex);
            
            this.className = className;
        }
        
        public Token getClassName()
        {
            return className;
        }
        
        public String getClassNameText()
        {
            return className.getTextContent();
        }
        
        public void setBaseClass(Token baseClass) 
        {
            this.baseClass = baseClass;
        }
        
        public long getClassID()
        {
            return classID;
        }
        
        @Override
        <T> T accept(Visitor<T> visitor)
        {
            return visitor.visitClassDef(this);
        }

        @Override
        public int hashCode()
        {
            int hash = 5;
            hash = 53 * hash + Objects.hashCode(this.className);
            hash = 53 * hash + Objects.hashCode(this.baseClass);
            hash = 53 * hash + (int) (this.classID ^ (this.classID >>> 32));
            hash = 53 * hash + (int) (this.getStartingLineIndex() ^ 
                (this.getStartingLineIndex() >>> 32));
            hash = 53 * hash + (int) (this.getEndingLineIndex() ^ 
                (this.getEndingLineIndex() >>> 32));
            return hash;
        }

        @Override
        public boolean equals(Object obj)
        {
            if (this == obj)
            {
                return true;
            }
            if (obj == null)
            {
                return false;
            }
            if (getClass() != obj.getClass())
            {
                return false;
            }
            if (!super.equals(obj))
            {
                return false;
            }
            
            final ClassDef other = (ClassDef) obj;
            if (this.classID != other.classID)
            {
                return false;
            }
            if (!Objects.equals(this.className, other.className))
            {
                return false;
            }
            if (!Objects.equals(this.baseClass, other.baseClass))
            {
                return false;
            }
            return true;
        }
    }
    
    public static class StructDef extends Block
    {
        public StructDef(long startingLineIndex)
        {
            super(startingLineIndex);
        }
        
        @Override
        <T> T accept(Visitor<T> visitor)
        {
            return visitor.visitStructDef(this);
        }
        
        @Override
        public boolean needsSemicolon()
        {
            return true;
        }
        
        // .equals and .hashCode from Block should be fine for this class
    }
    
    public static class While extends Block
    {
        private Expression exitCondition;
    
        public While(long startingLineIndex)
        {
            super(startingLineIndex);
            initDefaultExitCondition();
        }
        
        private void initDefaultExitCondition()
        {
            this.setExitCondition(ExpressionHandler.
                createNullLiteral(TokenFileInfo.NO_FILEPATH,
                (int) this.getStartingLineIndex()));
        }
        
        public Expression getExitCondition()
        {
            return exitCondition;
        }

        public void setExitCondition(Expression exitCondition)
        {
            this.exitCondition = exitCondition;
        }

        @Override
        public int hashCode()
        {
            int hash = 7;
            hash = 47 * hash + Objects.hashCode(this.exitCondition);
            hash = 47 * hash + Objects.hashCode(this.getBody());
            hash = 47 * hash + (int) (this.getStartingLineIndex() ^ 
                (this.getStartingLineIndex() >>> 32));
            hash = 47 * hash + (int) (this.getEndingLineIndex() ^ 
                (this.getEndingLineIndex() >>> 32));
            return hash;
        }

        @Override
        public boolean equals(Object obj)
        {
            if (this == obj)
            {
                return true;
            }
            if (obj == null)
            {
                return false;
            }
            if (getClass() != obj.getClass())
            {
                return false;
            }
            if (!super.equals(obj))
            {
                return false;
            }
            
            final While other = (While) obj;
            if (!Objects.equals(this.exitCondition, other.exitCondition))
            {
                return false;
            }
            return true;
        }
        
        
        @Override
        <T> T accept(Visitor<T> visitor)
        {
            return visitor.visitWhile(this);
        }
        
        // TODO - fix .equals and .hashCode to include the new vars as well
    }
    
    public static class Return extends Statement
    {
        private Statement returnedStatement;
        
        public Return(Statement returnedStatement, long startingLineIndex)
        {
            super(startingLineIndex);
            this.returnedStatement = returnedStatement;
        }
        
        public Statement getReturnedStatement()
        {
            return returnedStatement;
        }

        @Override
        public int hashCode()
        {
            int hash = 3;
            hash = 79 * hash + Objects.hashCode(this.returnedStatement);
            hash = 79 * hash + (int) (this.getStartingLineIndex() ^ 
                (this.getStartingLineIndex() >>> 32));
            hash = 79 * hash + (int) (this.getEndingLineIndex() ^ 
                (this.getEndingLineIndex() >>> 32));
            return hash;
        }

        @Override
        public boolean equals(Object obj)
        {
            if (this == obj)
            {
                return true;
            }
            if (obj == null)
            {
                return false;
            }
            if (getClass() != obj.getClass())
            {
                return false;
            }
            if (!super.equals(obj))
            {
                return false;
            }
            
            final Return other = (Return) obj;
            if (!Objects.equals(this.returnedStatement, other.returnedStatement))
            {
                return false;
            }
            return true;
        }
        
        @Override
        <T> T accept(Visitor<T> visitor)
        {
            return visitor.visitReturn(this);
        }
    }
    
    public static class ExpressionStatement extends Statement
    {
        private final Expression expression;
        
        public ExpressionStatement(Expression expression, long startingLineIndex)
        {
            super(startingLineIndex);
            this.expression = expression;
        }
        
        @Override
        <T> T accept(Visitor<T> visitor)
        {
            return visitor.visitExpressionStatement(this);
        }
        
        public Expression getExpression()
        {
            return expression;
        }

        @Override
        public int hashCode()
        {
            int hash = 3;
            hash = 89 * hash + Objects.hashCode(this.expression);
            hash = 89 * hash + (int) (this.getStartingLineIndex() ^ 
                (this.getStartingLineIndex() >>> 32));
            hash = 89 * hash + (int) (this.getEndingLineIndex() ^ 
                (this.getEndingLineIndex() >>> 32));
            return hash;
        }

        @Override
        public boolean equals(Object obj)
        {
            if (this == obj)
            {
                return true;
            }
            if (obj == null)
            {
                return false;
            }
            if (getClass() != obj.getClass())
            {
                return false;
            }
            if (!super.equals(obj))
            {
                return false;
            }
            
            final ExpressionStatement other = (ExpressionStatement) obj;
            if (!Objects.equals(this.expression, other.expression))
            {
                return false;
            }
            return true;
        }
    }
    
    public static class VarDeclaration extends Statement
    {
        public static enum Sign
        {
            SIGNED,
            UNSIGNED,
            UNSPECIFIED
        }
        
        public static enum SizeModifier
        {
            SHORT,
            LONG,
            LONG_LONG,
            UNSPECIFIED;
            
            public String getActualModifierText()
            {
                return name().toLowerCase().replace("_", " ");
            }
        }
        
        public static enum SimplifiedType
        {
            CHAR,
            SHORT,
            INT,
            LONG,
            LONG_LONG,
            FLOAT,
            DOUBLE,
            LONG_DOUBLE,
            VOID,
            STRUCT,
            CLASS
        }
        
        public static final Token       NO_ASSIGNMENT   = null;
        public static final Expression  NO_VALUE        = null;
        
        /**
         * The ID of a variable that holds plain-old-data. 
         */
        public static final long        NOT_A_CLASS     = Long.MIN_VALUE;
        
        public static final String      RETURN_VAR_NAME = "";
        
        /**
         * How many indirections (asterisks) are declared for this variable
         */
        private final int               pointerLevel;
        private final Sign              sign;
        private final SimplifiedType    simplifiedType;
        private final Token             varName;
        private final Token             assignmentOperator;
        private final Expression        value;
        
        /**
         * If the SimplifiedType is CLASS, then this is set to the classID, 
         *  otherwise it's Long.MIN_VALUE.
         */
        private long classID = NOT_A_CLASS;
        
        public VarDeclaration(Sign sign, SimplifiedType simplifiedType, 
            int pointerLevel, Token varName, Token assignmentOperator, 
            Expression value, long startingLineIndex)
        {
            super(startingLineIndex);
            this.sign               = sign;
            this.simplifiedType     = simplifiedType;
            this.pointerLevel       = pointerLevel;
            this.varName            = varName;
            this.value              = value;
            this.assignmentOperator = assignmentOperator;
        }
        
        public Sign getSign()
        {
            return sign;
        }
        
        public SimplifiedType getSimplifiedType()
        {
            return simplifiedType;
        }
        
        public int getPointerLevel()
        {
            return pointerLevel;
        }
        
        public Token getName()
        {
            return varName;
        }
        
        public String getNameText()
        {
            return varName.getTextContent();
        }
        
        public Expression getValue()
        {
            return value;
        }
        
        public Token getAssignmentOperator()
        {
            return assignmentOperator;
        }
        
        public boolean isReturnVar()
        {
            return getNameText().equals(RETURN_VAR_NAME);
        }
        
        public void setClassID(long classID)
        {
            this.classID = classID;
        }

        public long getClassID()
        {
            return classID;
        }

        @Override
        public int hashCode()
        {
            int hash = 7;
            hash = 29 * hash + (int) (this.getStartingLineIndex() ^ 
                (this.getStartingLineIndex() >>> 32));
            hash = 29 * hash + (int) (this.getEndingLineIndex() ^ 
                (this.getEndingLineIndex() >>> 32));
            hash = 29 * hash + this.pointerLevel;
            hash = 29 * hash + Objects.hashCode(this.sign);
            hash = 29 * hash + Objects.hashCode(this.simplifiedType);
            hash = 29 * hash + Objects.hashCode(this.varName);
            hash = 29 * hash + Objects.hashCode(this.assignmentOperator);
            hash = 29 * hash + Objects.hashCode(this.value);
            hash = 29 * hash + (int) (this.classID ^ (this.classID >>> 32));
            return hash;
        }

        @Override
        public boolean equals(Object obj)
        {
            if (this == obj)
            {
                return true;
            }
            if (obj == null)
            {
                return false;
            }
            if (getClass() != obj.getClass())
            {
                return false;
            }
            if (!super.equals(obj))
            {
                return false;
            }
            
            final VarDeclaration other = (VarDeclaration) obj;
            if (this.pointerLevel != other.pointerLevel)
            {
                return false;
            }
            if (this.classID != other.classID)
            {
                return false;
            }
            if (this.sign != other.sign)
            {
                return false;
            }
            if (this.simplifiedType != other.simplifiedType)
            {
                return false;
            }
            if (!Objects.equals(this.varName, other.varName))
            {
                return false;
            }
            if (!Objects.equals(this.assignmentOperator, other.assignmentOperator))
            {
                return false;
            }
            if (!Objects.equals(this.value, other.value))
            {
                return false;
            }
            return true;
        }
        
        @Override
        <T> T accept(Visitor<T> visitor)
        {
            return visitor.visitVarDeclaration(this);
        }
    }
    
    public static class Package extends Statement
    {
        public static final String  NO_PACKAGE = null;
        
        /**
         * JS 3/24/2021 : I don't think this is used anywhere but I'm leaving it
         *  in until the package class has been implemented, just in case this
         *  was a planned future refactoring.
         */
        //public static final int     NO_LINE_INDEX = -1;
        
        private String fullyQualifiedPackageName;
        
        public Package(String fullyQualifiedPackageName, long startingLineIndex)
        {
            super(startingLineIndex);
            this.fullyQualifiedPackageName = fullyQualifiedPackageName;
        }
        
        public String getFullyQualifiedPackageName()
        {
            return fullyQualifiedPackageName;
        }

        @Override
        public int hashCode()
        {
            int hash = 3;
            hash = 41 * hash + Objects.hashCode(this.fullyQualifiedPackageName);
            hash = 41 * hash + (int) (this.getStartingLineIndex() ^ 
                (this.getStartingLineIndex() >>> 32));
            hash = 41 * hash + (int) (this.getEndingLineIndex() ^ 
                (this.getEndingLineIndex() >>> 32));
            return hash;
        }

        @Override
        public boolean equals(Object obj)
        {
            if (this == obj)
            {
                return true;
            }
            if (obj == null)
            {
                return false;
            }
            if (getClass() != obj.getClass())
            {
                return false;
            }
            if (!super.equals(obj))
            {
                return false;
            }
            
            final Package other = (Package) obj;
            if (!Objects.equals(this.fullyQualifiedPackageName, 
                other.fullyQualifiedPackageName))
            {
                return false;
            }
            return true;
        }
        
        @Override
        <T> T accept(Visitor<T> visitor)
        {
            return visitor.visitPackage(this);
        }
    }
    
    public Statement(long startingLineIndex)
    {
        this.startingLineIndex = startingLineIndex;
    }
    
    
    abstract <T> T accept(Visitor<T> visitor);
    
    public boolean needsSemicolon()
    {
        return true;
    }
    
    /**
     * Returns the line the statement started on.
     */
    public long getStartingLineIndex()
    {
        return startingLineIndex;
    }
    
    public void setStartingLineIndex(long startingLineIndex)
    {
        this.startingLineIndex = startingLineIndex;
    }
    
    /**
     * Returns the line the statement ended on.
     */
    public long getEndingLineIndex()
    {
        return endingLineIndex;
    }
    
    public void setEndingLineIndex(long endingLineIndex)
    {
        this.endingLineIndex = endingLineIndex;
    }
    
    @Override
    public boolean equals(Object other)
    {
        if (other instanceof Statement)
        {
            Statement otherStmt = (Statement) other;
            return 
                getStartingLineIndex() == otherStmt.getStartingLineIndex() &&
                getEndingLineIndex() == otherStmt.getEndingLineIndex();
        }
        else
        {
            return false;
        }
    }

    @Override
    public int hashCode()
    {
        int hash = 7;
        hash = 83 * hash + (int) (this.startingLineIndex ^ 
            (this.startingLineIndex >>> 32));
        hash = 83 * hash + (int) (this.endingLineIndex ^ 
            (this.endingLineIndex >>> 32));
        return hash;
    }
}