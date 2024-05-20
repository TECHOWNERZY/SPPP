#include <stdio.h>
#include <string.h>
#include <stdlib.h>
#include <stdbool.h>

struct MDT
{
    char name[25];
    int index;
    struct MDT *next;
};

struct MNT
{
    int index;
    char name[25];
    int MDTIndex;
    struct MNT *next;
};

struct MNT *insertMNT(struct MNT *head, char macro[], int idx, int mdt)
{
    struct MNT *temp = (struct MNT *)malloc(sizeof(struct MNT));
    strcpy(temp->name, macro);
    temp->index = idx;
    temp->MDTIndex = mdt;
    temp->next = NULL;

    if (head == NULL)
    {
        head = temp;
        return head;
    }
    struct MNT *start = head;
    while (start->next != NULL)
        start = start->next;
    start->next = temp;
    // free(temp);
    return head;
}

struct MDT *insertMDT(struct MDT *head, char macro[], int idx)
{
    // printf("%d   %s", idx, macro);
    struct MDT *temp = (struct MDT *)malloc(sizeof(struct MDT));
    strcpy(temp->name, macro);
    temp->index = idx;
    temp->next = NULL;

    if (head == NULL)
    {
        // printf("%d   %s", temp->index, temp->name);
        head = temp;
        return head;
    }
    struct MDT *start = head;
    while (start->next != NULL)
        start = start->next;
    start->next = temp;
    // printf("%d   %s", start->index, start->name);
    // free(temp);
    return head;
}

int startsWith(const char *str, const char *prefix)
{
    int flag = 1;
    int j = 0;
    for (int i = 0; prefix[i] != '\0'; i++)
    {
        while (str[j] == ' ')
        {
            j++;
        }
        if (str[j] != prefix[i])
        {
            flag = 0;
            break;
        }
        j++;
    }
    return flag;
}

char *extractMacro(char line[])
{
    int length = strlen(line);
    char *result;
    char *spaceAfterMacro = strchr(line, ' ');
    result = (char *)malloc(strlen(spaceAfterMacro + 1 + 1));
    strncpy(result, spaceAfterMacro + 1, length - 1);
    result[length - 1] = '\0';
    return result;
}
void removeSpaces(char str[])
{
    int count = 0;
    for (int i = 0; str[i]; i++)
    {
        if (str[i] != ' ')
        {
            str[count++] = str[i];
        }
    }
    str[count] = '\0';
}

int searchMacro(struct MNT *head, char macro[])
{
    struct MNT *start = head;

    while (start != NULL)
    {
        // printf("\nMDT Table\n");

        if (startsWith(start->name, macro))
        {
            // printf("macro found  %d %s", start->MDTIndex, start->name);
            return start->MDTIndex;
        }
        start = start->next;
    }
    return -1;
}

int main()
{
    FILE *fp;
    fp = fopen("simpleMacroInput.txt", "r");
    if (fp == NULL)
    {
        perror("Error opening file");
        return 1;
    }

    char line[1000];
    struct MNT *MNThead = NULL;
    struct MDT *MDThead = NULL;
    int NPTR = 1;
    int DPTR = 1;
    bool flag = false;

    while (fgets(line, sizeof(line), fp) != NULL)
    {
        if (flag)
            break;
        else if (startsWith(line, "macro"))
        {
            strcpy(line, extractMacro(line));
            if (strcmp(line, "\n") == 0)
            {
                fgets(line, sizeof(line), fp);
            }
            MNThead = insertMNT(MNThead, line, NPTR, DPTR);
            NPTR++;
        }
        else if (startsWith(line, ".code"))
        {
            printf("\nExpanded code: \n");
            while (fgets(line, sizeof(line), fp) != NULL)
            {

                if (startsWith(line, "end"))
                {
                    flag = true;
                    break;
                }
                else
                {
                    int ind = searchMacro(MNThead, line);
                    if (ind > 0)
                    {
                        struct MDT *start = MDThead;
                        while (start != NULL)
                        {
                            if (start->index >= ind)
                            {
                                if (strstr(start->name, "mend"))
                                {
                                    break;
                                }
                                printf("%s", start->name);
                            }
                            start = start->next;
                        }
                    }
                }
            }
        }
        else
        {
            MDThead = insertMDT(MDThead, line, DPTR);
            DPTR++;
        }
    }
    fclose(fp);

    struct MNT *start2 = MNThead;
    printf("\nMNT Table\n");
    while (start2 != NULL)
    {
        printf("\n%d   %s   %d", start2->index, start2->name, start2->MDTIndex);
        start2 = start2->next;
    }

    struct MDT *start = MDThead;
    printf("\nMDT Table\n");
    while (start != NULL)
    {
        printf("\n%d   %s", start->index, start->name);
        start = start->next;
    }

    return 0;
}