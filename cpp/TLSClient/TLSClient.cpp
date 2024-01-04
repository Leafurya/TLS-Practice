#define _WINSOCK_DEPRECATED_NO_WARNINGS
#include <iostream>
#include <winsock2.h>
#include <openssl/ssl.h>
#include <openssl/err.h>
#include <openssl/x509.h>

#pragma comment(lib,"libssl.lib")
#pragma comment(lib,"libcrypto.lib")
#pragma comment(lib,"ws2_32.lib")

#define CHK_NULL(x) if((x) == NULL) exit(1);
#define CHK_ERR(err, s) if((err) == -1) { perror(s); exit(1); }
#define CHK_SSL(err) if((err) == -1) { ERR_print_errors_fp(stderr); exit(2); }

int main()
{
    const SSL_METHOD* meth;
    SSL_CTX* ctx;
    SSL* ssl;

    SSL_load_error_strings();
    SSLeay_add_ssl_algorithms();

    meth = SSLv23_client_method();
    ctx = SSL_CTX_new(meth);
    CHK_NULL(ctx);

    SOCKET sock;
    SOCKADDR_IN addr;
    WSADATA wsaData;
    int err;

    WSAStartup(MAKEWORD(2, 2), &wsaData);
    sock = socket(AF_INET, SOCK_STREAM, 0);
    CHK_ERR(sock, "socket");

    memset(&addr, 0, sizeof(addr));
    addr.sin_family = AF_INET;
    addr.sin_port = htons(2025);
    addr.sin_addr.S_un.S_addr = inet_addr("192.168.1.70");

    err = connect(sock, (SOCKADDR*)&addr, sizeof(addr));
    CHK_ERR(err, "connect");
    ssl = SSL_new(ctx);
    CHK_NULL(ssl);

    SSL_set_fd(ssl, sock);
    err = SSL_connect(ssl);
    CHK_NULL(err);

    X509* server_cert;
    char* str;

    server_cert = SSL_get_peer_certificate(ssl);
    CHK_NULL(server_cert);
    printf("Server cert: \n");

    str = X509_NAME_oneline(X509_get_subject_name(server_cert), 0, 0);
    CHK_NULL(str);
    printf("t subject : %s\n", str);
    OPENSSL_free(str);

    str = X509_NAME_oneline(X509_get_issuer_name(server_cert), 0, 0);
    CHK_NULL(str);
    printf("t issuer : %s\n", str);
    OPENSSL_free(str);

    X509_free(server_cert);

    err = SSL_write(ssl, "hello TLS server!", strlen("hello TLS server!"));
    CHK_SSL(err);

    char buf[1024] = { 0 };
    err = SSL_read(ssl, buf, sizeof(buf));
    CHK_SSL(err);
    printf("recv: %s\n", buf);

    closesocket(sock);
    SSL_free(ssl);
    SSL_CTX_free(ctx);
    WSACleanup();
    return 0;
}