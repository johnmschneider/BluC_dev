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
package bluC.transpiler.scope.rootUnrelatedTests;

import bluC.builders.VarDeclarationBuilder;
import bluC.transpiler.Scope;
import bluC.transpiler.Statement;
import static bluC.transpiler.scope.ScopeTestUtils.createRootScope;
import java.util.ArrayList;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author John Schneider
 */
public class VariableTest
{
    @Test
    public void testGetVariablesInThisScope_varCountIsCorrect()
    {
        Scope           root;
        String          mockFileName;
        int             mockLineIndex;
        VarDeclarationBuilder
                        varBuilder;
        Statement.VarDeclaration 
                        mockVarDecl;
        Statement.VarDeclaration 
                        mockVarDecl2;

        root            = createRootScope();
        mockFileName    = "ScopeTest_RootUnrelatedTests.java";
        mockLineIndex   = 2;

        varBuilder      = new VarDeclarationBuilder();
        varBuilder.
            setFileName(mockFileName).
            setStartingLineIndex(mockLineIndex);

        mockVarDecl     = varBuilder. 
            setVarName("varTest").
            setSimplifiedType(Statement.VarDeclaration.SimplifiedType.LONG).
            setSignedness(Statement.VarDeclaration.Sign.UNSIGNED).
            build();

        mockVarDecl2    = varBuilder.
            setVarName("varTest2").
            setSimplifiedType(Statement.VarDeclaration.SimplifiedType.CHAR).
            setSignedness(Statement.VarDeclaration.Sign.UNSPECIFIED).
            build();

        root.addVariableToScope(mockVarDecl);
        root.addVariableToScope(mockVarDecl2);

        int             variableCount;
        ArrayList<Statement.VarDeclaration>
                        variables;

        variables       = root.getVariablesInThisScope();
        variableCount   = variables.size();

        assertEquals(2, variableCount);
    }

    @Test
    public void testGetVariablesInThisScope_varTypeIsCorrect()
    {
        Scope           root;
        String          mockFileName;
        int             mockLineIndex;
        VarDeclarationBuilder
                        varBuilder;
        Statement.VarDeclaration 
                        mockVarDecl;
        Statement.VarDeclaration 
                        mockVarDecl2;

        root            = createRootScope();
        mockFileName    = "ScopeTest_RootUnrelatedTests.java";
        mockLineIndex   = 2;

        varBuilder      = new VarDeclarationBuilder();
        varBuilder.
            setFileName(mockFileName).
            setStartingLineIndex(mockLineIndex);

        mockVarDecl     = varBuilder. 
            setVarName("typeTest").
            setSimplifiedType(Statement.VarDeclaration.SimplifiedType.CHAR).
            setSignedness(Statement.VarDeclaration.Sign.UNSIGNED).
            build();

        mockVarDecl2    = varBuilder.
            setVarName("typeTestNo2").
            setSimplifiedType(Statement.VarDeclaration.SimplifiedType.DOUBLE).
            setSignedness(Statement.VarDeclaration.Sign.UNSPECIFIED).
            build();

        root.addVariableToScope(mockVarDecl);
        root.addVariableToScope(mockVarDecl2);

        int             variableCount;
        ArrayList<Statement.VarDeclaration>
                        variables;

        variables       = root.getVariablesInThisScope();

        assertEquals(mockVarDecl, variables.get(0));
        assertEquals(mockVarDecl2, variables.get(1));
    }

    @Test
    public void testAddVariableToScope()
    {
        Scope           root;
        String          mockFileName;
        int             mockLineIndex;
        VarDeclarationBuilder
                        varBuilder;
        Statement.VarDeclaration 
                        mockVarDecl;
        Statement.VarDeclaration 
                        mockVarDecl2;

        root            = createRootScope();
        mockFileName    =
            "ScopeTest_RootUnrelatedTests_testAddVariableToScope" +
            ".java";
        mockLineIndex   = 2;

        varBuilder      = new VarDeclarationBuilder();
        varBuilder.
            setFileName(mockFileName).
            setStartingLineIndex(mockLineIndex);

        mockVarDecl     = varBuilder. 
            setVarName("scopeTest01").
            setSimplifiedType(Statement.VarDeclaration.SimplifiedType.LONG).
            setSignedness(Statement.VarDeclaration.Sign.UNSIGNED).
            build();

        mockVarDecl2    = varBuilder.
            setVarName("scopeTest02").
            setSimplifiedType(Statement.VarDeclaration.SimplifiedType.CHAR).
            setSignedness(Statement.VarDeclaration.Sign.SIGNED).
            build();

        root.addVariableToScope(mockVarDecl);
        root.addVariableToScope(mockVarDecl2);

        int             variableCount;
        ArrayList<Statement.VarDeclaration>
                        variables;

        variables       = root.getVariablesInThisScope();

        assertEquals(mockVarDecl, variables.get(0));
        assertEquals(mockVarDecl2, variables.get(1));
    }
}
