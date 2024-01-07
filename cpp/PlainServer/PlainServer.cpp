#define _WINSOCK_DEPRECATED_NO_WARNINGS

#include <iostream>
#include <winsock2.h>
#pragma comment(lib,"ws2_32.lib")
int main()
{
    SOCKET servSock;
    SOCKET clntSock;

    WSADATA wsaData;
    SOCKADDR_IN servAddr;
    WSAStartup(MAKEWORD(2, 2), &wsaData);
    servSock = socket(PF_INET, SOCK_STREAM, 0);

    if (!servSock) {
        printf("socket error\n");
        return 0;
    }

    memset(&servAddr, 0, sizeof(servAddr));
    servAddr.sin_family = AF_INET;
    servAddr.sin_addr.s_addr = htonl(INADDR_ANY);
    servAddr.sin_port = htons(2025);

    if (bind(servSock, (SOCKADDR*)&servAddr, sizeof(servAddr)) == SOCKET_ERROR) {
        printf("bind error\n");
        return 0;
    }

    listen(servSock, 5);

    SOCKADDR_IN clntAddr;
    int addrLen = sizeof(clntAddr);
    clntSock = accept(servSock, (SOCKADDR*)&clntAddr, &addrLen);
    printf("connect: %s\n", inet_ntoa(clntAddr.sin_addr));

    char buf[1024] = { 0 };

    recv(clntSock, buf, sizeof(buf), 0);
    printf("recv data: %s\n", buf);

    send(clntSock, buf, strlen(buf), 0);

    closesocket(servSock);
    closesocket(clntSock);
    WSACleanup();

    return 1;
}