#include <stdio.h>
#include <unistd.h>
#include <sys/stat.h>
#include <unistd.h>
int main()
{
    int result = 0;
    int element[10] = {1,2,3,4,5,6,7,8,9,10};
    int i;
    for(i = 0;i < 10;i++)
    {
        result += element[i];
    }
    printf("result : %d\n",result);
    int* status;
    printf("parent process finishes\n");

    return 0;

}