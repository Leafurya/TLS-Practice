#define _WINSOCK_DEPRECATED_NO_WARNINGS
#define _CRT_SECURE_NO_WARNINGS

#include <iostream>
#include <winsock2.h>
#include <openssl/ssl.h>
#include <openssl/err.h>

#pragma comment(lib,"libssl.lib")
#pragma comment(lib,"libcrypto.lib")
#pragma comment(lib,"ws2_32.lib")

//#define CRT "D:\\OpenSSL\\private\\private.crt"
//#define KEY "D:\\OpenSSL\\private\\private.key"
#define CRT "E:\\desktop-share\\private\\private.crt"
#define KEY "E:\\desktop-share\\private\\private.key"

int main()
{
    SSL_load_error_strings();
    SSLeay_add_ssl_algorithms();

    const SSL_METHOD* meth;
    SSL_CTX* ctx;

    int err;

    meth = SSLv23_server_method();
    ctx = SSL_CTX_new(meth);
    if (!ctx) {
        ERR_print_errors_fp(stderr);
        exit(2);
    }

    if (SSL_CTX_use_certificate_file(ctx, CRT, SSL_FILETYPE_PEM) <= 0) {
        ERR_print_errors_fp(stderr);
        exit(3);
    }
    if (SSL_CTX_use_PrivateKey_file(ctx, KEY, SSL_FILETYPE_PEM) <= 0) {
        ERR_print_errors_fp(stderr);
        exit(4);
    }
    if (!SSL_CTX_check_private_key(ctx)) {
        fprintf(stderr, "Private key does not match the certificate public key\n");
        exit(5);
    }
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
    SSL* ssl = SSL_new(ctx);

    if (!ssl) {
        printf("ssl is null\n");
        exit(1);
    }
    SSL_set_fd(ssl, clntSock);
    err = SSL_accept(ssl);
    if ((err) == -1) { ERR_print_errors_fp(stderr); exit(2); }

    char buf[1024] = { 0 };

    err = SSL_read(ssl, buf, sizeof(buf));
    if ((err) == -1) { ERR_print_errors_fp(stderr); exit(2); }

    printf("recv data: %s\n", buf);

    err = SSL_write(ssl, "Hello TLS!", 11);
    if ((err) == -1) { ERR_print_errors_fp(stderr); exit(2); }

    closesocket(servSock);
    closesocket(clntSock);
    WSACleanup();

    SSL_free(ssl);
    SSL_CTX_free(ctx);

    return 1;
}