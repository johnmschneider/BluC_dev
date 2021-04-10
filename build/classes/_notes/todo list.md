<body style="background-color:LightGrey">
<pre>
<h1>Todo list</h1>

<h2>Misc</h2>
1. implement macros
2. handle #elif directive
3. handle #else directive
4. add support for multiline #define's (aka \ character)
5. implement default string type being string class
6. (parseTree.txt) new class begins at line 1042      // <<<< what is this? i forget
7. change all references to "filepath, filePath, filename, fileName" to just "filePath"
8. double check all of the custom .equals methods to see if they need
    refactoring
<br>
<h2>Refactoring</h2>
1. refactor out nullSafeEquals because java's Object<b><u>s</u></b>.equals (NOT Objec<b><u>t</u></b>.equals)
    checks for null
2. refactor Expression.Literal to use the Operand field from Expression instead
    of its own unique "value" field
3. either update lineIndex to be int or update currentTokenIndex to be long
</pre>
<pre>
## Features to add
<h2> unsafe: </h2><h3>    SYMBOLS RESERVED:
        unsafe [symbol[[var-declaration *] || address-of || block || function || 
                        method || class]]
<h4>    description</h4>        -Disables all safety checks for the symbol specified and uses the fastest 
            construct (in general, the most c-like construct) for this symbol 
            instead.
            
        - If the symbol is a block, then this keyword applies <b>unsafe</b> to each symbol
            inside the block.
            
        - Used where maximum flexibility is needed, but where the compiler can no
            longer gaurantee the safety of the resultant code.
        
        - Should be a last-resort, the language generally compiles to extremely
            efficient code with minimal overhead from safety checks
<h4>    example </h4>        <text style="color:purple;">int</text>  myVarWithValue = <text style="color:DodgerBlue;">2112</text>;
        <text style="color:blue;">unsafe</text>
        {
            <text style="color:grey;">// normally (outside of an unsafe block), this would be a reference,
            //    but here it's a pointer</text>
            <text style="color:purple;">int</text>& myPtr = &myVarWithValue<text>;</text> <!-- avoid html parsing this as html entity-->
        }
<h2> references: </h2><h3>    SYMBOLS RESERVED:
        [type]& [variable-name] <text style="color:gray"><i>reference declaration</i></text>
        &[variable] <text style="color:gray"><i>reference address-of operator</i></text></h3>
<h4>    description</h4>        - A const (immutable) pointer on which pointer arithmetic is not allowed.
        
        - References can only be created by using the address of operator (&) on a variable.

        - You can't take the reference of a reference, since references are immutable.
<h4>    example</h4>        <text style="color:purple;">int</text>  myVarWithValue = <text style="color:DodgerBlue;">2112</text>;
        <text style="color:purple;">int</text>& myRef          = &myVarWithValue<text>;</text> <!-- avoid html parsing this as html entity-->
<h2> pointers: </h2><h3>    SYMBOLS RESERVED:
        unsafe [type]* [variable-name] <text style="color:gray"><i>(pointer declaration)</i></text>
        unsafe &[variable-name] <text style="color:gray"><i>(pointer address-of operator)</i></text>
        unsafe *[variable-name] <text style="color:gray"><i>(pointer dereference)</i></text>
        
        <text style="color:Red">(inside unsafe block)</text>
        [type]* [variable-name] <text style="color:gray"><i>(pointer declaration)</i></text>
        &[variable-name] <text style="color:gray"><i>(pointer address-of operator)</i></text>
        *[variable-name] <text style="color:gray"><i>(pointer dereference)</i></text></h3>
<h4>    description</h4>        - A raw c-style pointer with pointer arithmetic and no safety checks. 

        - It's highly recommended that you use an array type for contiguous
            data, and a reference type to change a variable not in the
            current scope.

        - This feature exists because raw pointers offer more flexibility
            for things such as interleaved data, and <u><i>may</i></u> be faster than
            references in certain scenarios (see the reference entry
            for more details on this).
            
        - The address-of operator always operates on pointers inside of an 
            unsafe block.
<h4>    example</h4>        <text style="color:purple;">int</text>  myVarWithValue = <text style="color:DodgerBlue;">2112</text>;
        <text style="color:blue;">unsafe</text> <text style="color:purple;">int</text>& myPtr   = <text style="color:blue;">unsafe</text> &myVarWithValue<text>;</text> <!-- avoid html parsing this as html entity-->
        
        
        - An alternative syntax is offered for longer blocks in which all 
            safety checks should be disabled:
                
        <text style="color:purple;">int</text>  myVarWithValue = <text style="color:DodgerBlue;">2112</text>;
        <text style="color:blue;">unsafe</text>
        {
            <text style="color:grey;">// normally (outside of an unsafe block), this would be a reference,
            //    but here it's a pointer</text>
            <text style="color:purple;">int</text>& myPtr = &myVarWithValue<text>;</text> <!-- avoid html parsing this as html entity-->
        }

<h2> inline:</h2><h3>    KEYWORDS RESERVED:
        <i>(reserved everywhere)</i>
        inline [function || method || getter || setter]
        
    ERRORS RESERVED:
        CANT_INLINE </h3>
<h4>    description</h4>        - Tells the compiler to try and inline a <b>function</b>, <b>method</b>, or <b>getter/setter</b>. Note that
            a single-line <b>getter/setter</b> is marked as <text style="color:blue;font-weight:bold">inline</text> by default.

        - If inlining isn't possible, then a <text style="color:red;font-weight:bold">CANT_INLINE</text> error is raised. The specific
            message produced by this error is left to the compiler, but it must at least
            output the <text style="color:red;font-weight:bold">CANT_INLINE</text> token.
<h4>    example</h4>        - On inlining, the folowing code ...

        <text style="color:blue;font-weight:bold">class</text> <text style="color:purple;">Example</text>
        {
        <text style="color:blue;font-weight:bold">private:</text>
            <text style="color:purple;">int</text> count;

        <text style="color:blue;font-weight:bold">public:</text>
            <text style="color:blue;font-weight:bold">inline</text> <text style="color:purple;">void</text> incrementCount()
            {
                count++;
            }
        }
        
        <text style="color:purple">int</text> main()
        {
            <text style="color:purple;">Example</text> ex;
            ex.incrementCount();
            
            <text style="color:blue;font-weight:bold">return</text> <text style="color:DodgerBlue">0</text>;
        }
        
        
        - ... gets inlined in main like so ...
        
        <text style="color:purple">int</text> main()
        {
            <text style="color:purple;">Example</text> ex;
            ex.count ++;
            
            <text style="color:blue;font-weight:bold">return</text> <text style="color:DodgerBlue">0</text>;
        }
        
        - ... but the compiler ensures that count remains private. 
            
            (As private as it can be -- as with all private fields in
                any language with unchecked pointers; accessing the 
                pointer address-of a class instance, offsetting it by
                some amount, and then assigning a value to the memory
                address at that location may still be able to change
                the value of a private field).
<h2> set/get: </h2><h3>    KEYWORDS RESERVED:
        <i>(reserved in highest scope inside a class)</i>
        set [setter-name]
        
        <i>(reserved in highest scope inside a class)</i>
        get [getter-name]
        
        (reserved variable name inside a <text style="color:blue;font-weight:bold">set</text> block with no parameter)
        [left-operand] value
        value [right-operand]</h3>
<h4>    description</h4>        - Indicates that this function should be called with either the <b>set</b> or <b>get</b> syntax.

        - Single-statement <b>set/get</b> functions are automatically inlined.

<h4>    single-statement set/get</h4>        - I can probably automatically optimize this to actually just operate on the variable.
<h4>        Syntactic sugar #1 for single-statement set/get</h4>            - This is the most terse way to write a setter/getter, which simply sets or gets
                the field it refers to:

            <text style="color:blue;font-weight:bold">class</text> <text style="color:purple;">SyntacticSugar1Example</text>
            {
            <text style="color:blue;font-weight:bold">private:</text>
                <text style="color:purple;">int</text> someProperty;

            <text style="color:blue;font-weight:bold">public:</text>
                <text style="color:blue;font-weight:bold">set</text> SomeProperty;
                <text style="color:blue;font-weight:bold">get</text> SomeProperty;
            }
        
            - The setter/getter can't have the exact same capitalization as a field as
                this would make certain statements ambiguous.
                
            - Because of this, any properties that use this syntactic sugar must be named
                uniquely, <u><b>regardless of letter casing</b></u>. If you have an "int someProperty"
                and "int sOmEPrOpErTy", then you can't use the simple single-statement
                getter/setter.
                
                - You can, however, use syntactic sugar #2 for single-statement
                    getters/setters syntax. It merely requires specification of
                    <i>which</i> property the getter/setter is referring to.
                    
                - You can also use the multi-line getter/setter syntax: if you
                    only include one statement inside the block of the multi-line
                    syntax then it will also be inlined by default.
<h4>        Syntactic sugar #2 for single-statement set/get</h4>            - This syntax is not as ambiguous as Syntactic sugar #1, so the "letter casing"
                rule for automatic property symbol deduction does not apply.

            <text style="color:blue;font-weight:bold">class</text> <text style="color:purple;">SyntacticSugar2Example</text>
            {
            <text style="color:blue;font-weight:bold">private:</text>
                <text style="color:purple;">int</text> someProperty;
                <text style="color:purple;">int</text> SOMEProperty;

            <text style="color:blue;font-weight:bold">public:</text>    
                <text style="color:blue;font-weight:bold">set</text> SomeProperty(someProperty);
                <text style="color:blue;font-weight:bold">get</text> SomeProperty(someProperty);

                <text style="color:blue;font-weight:bold">set</text> SOMEproperty(SOMEProperty);
                <text style="color:blue;font-weight:bold">get</text> SOMEproperty(SOMEProperty);
            }
            
            - Whilst it is possible to unambiguously differentiate between two variables
                whose names only differ in case, this is generally a very bad practice
                and should be avoided if at all possible.
<h4>        Single-statement set/get with multi-statement syntax</h4>            - This syntax is what the other single-statement forms desugar to. It's the
                least ambiguous, and the "letter casing" rule for property symbol deduction
                does not apply.

            - Automatic inlining still occurs, until such time that there is more than one
                statement inside the getter/setter block.

            - You can still explicitly inline getters/setters with multiple statements.

            <text style="color:blue;font-weight:bold">class</text> <text style="color:purple;">UnambiguousSingleStatementPropertyExample</text>
            {
            <text style="color:blue;font-weight:bold">private:</text>
                <text style="color:purple;">int</text> someProperty;

            <text style="color:blue;font-weight:bold">public:</text>
                <text style="color:blue;font-weight:bold">set</text> <text style="color:purple;">int</text> SomeProperty 
                {
                    someProperty = <text style="color:blue;font-weight:bold">value</text>;
                }

                <text style="color:blue;font-weight:bold">get</text> <text style="color:purple;">int</text> SomeProperty
                {
                    <text style="color:blue;font-weight:bold">return</text> someProperty;
                }
            }
<h4>        Multi-statement set/get</h4>            <text style="color:blue;font-weight:bold">class</text> <text style="color:purple;">Example</text>
            {
            <text style="color:blue;font-weight:bold">private:</text>
                <text style="color:purple;">int</text> someProperty;
                <text style="color:purple;">int</text> propertyModificationCount;
                <text style="color:purple;">int</text> propertyAccessCount;

            <text style="color:blue;font-weight:bold">public:</text>
                <text style="color:blue;font-weight:bold">set</text> property 
                {
                    someProperty = <text style="color:blue;font-weight:bold">value</text>;
                    propertyModificationCount++;
                }

                <text style="color:blue;font-weight:bold">get</text> property
                {
                    propertyAccessCount++;
                    <text style="color:blue;font-weight:bold">return</text> someProperty;
                }
            }
</pre>

2. add bluC.lang.StackFrame to each function/method as the first parameter (unless stack overflow protection is explicitly turned off with -AllowStackOverflow)
3. add thread_entry keyword that specifies when a new StackFrame has to be created (instead of just passed)
    <br><br>
    *used like this:*<br>
    <text style="color:blue;font-weight:bold">thread_entry</text> 
        <text style="color:purple;">void</text> exampleFunction(<text style="color:purple;">void</text>** args)
    { }
</body>