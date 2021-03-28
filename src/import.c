#include <stdio.h>

#define _NS(unmangledName) test_##unmangledName
    int _NS(var) = 2;

    int main(void)
    {
        printf("%d", _NS(var));
    }
#undef _NS