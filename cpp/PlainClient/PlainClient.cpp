#define _WINSOCK_DEPRECATED_NO_WARNINGS
#include <iostream>
#include <winsock2.h>

#pragma comment(lib,"ws2_32.lib")

int main()
{

    SOCKET sock;
    SOCKADDR_IN addr;
    WSADATA wsaData;
    int err;

    WSAStartup(MAKEWORD(2, 2), &wsaData);
    sock = socket(AF_INET, SOCK_STREAM, 0);

    memset(&addr, 0, sizeof(addr));
    addr.sin_family = AF_INET;
    addr.sin_port = htons(2025);
    addr.sin_addr.S_un.S_addr = inet_addr("192.168.0.3");

    err = connect(sock, (SOCKADDR*)&addr, sizeof(addr));

    char buf[1024] = "hello plain server";

    send(sock, buf, strlen(buf), 0);

    recv(sock, buf, sizeof(buf), 0);
    printf("recv: %s\n", buf);

    closesocket(sock);
    WSACleanup();
    return 0;
}