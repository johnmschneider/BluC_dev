int epsilon1 = "Hello";
int *ptrTest;
int ****bigPtrTest;
double a = 1 + 2.12 * (-3f * 1);
float b = -22f * a;
unsigned int c = 22;
long long d = 33;
long long d2 = 2;
float e = 11f;
double f = 22f;
long double g = 333f;
int a2;
int errorRecoveryTest = 22;
int errorRecoveryTest2;
a2 = c = 2;
int b2 = c--;
int c2 = ++b2;
{
    int c = 34;
    int secondVarTest = 23;
    int thirdVarTest = 43;
    {
        int blockInBlockTest = 234;
    }
 
}
 
{
    int bbtest1 = 11;
    int bbtest2 = 22;
}
 
int whatHappens(int paramTest1, char *paramTest2)
{
    int b = 3;
    {
        int a = 0;
    }
 
    if (a == 22)
    {
        int a55 = 33;
    }
    else if (a == 34)
    {
        int b55 = 35;
    }
    else
    {
        int c55 = 36;
    }

    if (a == 33)
    {
    }
    else if (a == 34)
    {
    }
    else
    {
        int elseblock = 0;
    }

    return 0;
}
 
int example(int funcScopeCloseTest)
{
    int test = 2;
}
 
