#include <stdio.h>
#include "lang/String.oxc"
#include "lang/Object.oxc"
#include "stdlib/Vector.oxc"

class Base1
{
	Object super;

	this()
	{
		this->super("1");
	}
}

class Base2
{
	Object super;

	this()
	{
		this->super("2");
	}
}


class Inherited1
{
	Base1 super;

	this()
	{
		this->super->super("1.1");
	}
}

class Inherited2
{
	Base2 super;

	this()
	{
		this->super->super("2.1");
	}
}

void runInheritanceTests()
{
	Inherited1 test1();
	Inherited2 test2();
	
	Base1 test1base();
	Base2 test2base();

	if (instanceOf((Object*) &test1, (Object*) &test1base))
	{
		printf("\n test1 instanceof base1");
	}
}

int main(int argc, char** argv)
{
	for (int i = 0; i < argc; i++)
	{
		String curArg(argv[i]);
		printf("curArg == %s\n", curArg->getCString());
	}

	Vector vecTest();

	for (int i = 0; i < 100; i++)
	{
		int* curInt = malloc(sizeof(int));
		*curInt = i;
		vecTest.add((void*) curInt);
	}

	for (int i = 0; i < 100; i++)
	{
		printf("%d, ", *(char*) vecTest.get(i));
	}

	runInheritanceTests();
	return 0;
}
