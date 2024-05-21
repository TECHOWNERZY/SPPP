#include <stdio.h>

int globalVar = 42;


void exampleFunction(int param1, char param2) {
    int localVar = param1 + globalVar;
    printf("Result: %d\n", localVar);
}

int main() {
    int x = 10;
    char y = 'A';
    float m;

    exampleFunction(x, y);

    return 0;
}
