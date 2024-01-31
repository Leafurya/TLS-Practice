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

    char request[] = ":authority: www.naver.com\n\r\
 :method: GET\n\r\
 :path: /\n\r\
 : scheme : https\n\r\
 accept : text / html, application / xhtml + xml, application / xml; q = 0.9, image / avif, image / webp, image / apng, */*;q=0.8,application/signed-exchange;v=b3;q=0.7\n\r\
 accept-encoding: gzip, deflate, br\n\r\
 accept-language: en-US,en;q=0.9\n\r\
 cache-control: max-age=0\n\r\
 cookie: PM_CK_loc=3c860616080e1d3213ae4c2aae7241c88ed50197fa2a85ace57cef8fb93d4169; SB_MODE=plusdeal; NNB=JDPYIFDNNA4WI\n\r\
 sec-ch-ua-mobile: ?0\n\r\
 sec-ch-ua-platform: \"Windows\"\n\r\
 sec-fetch-dest: document\n\r\
 sec-fetch-mode: navigate\n\r\
 sec-fetch-site: none\n\r\
 sec-fetch-user: ?1\n\r\
 upgrade-insecure-requests: 1\n\r\
 user-agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/111.0.0.0 Safari/537.36\n\r";

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
    addr.sin_port = htons(443);
    addr.sin_addr.S_un.S_addr = inet_addr("223.130.195.200");

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

    err = SSL_write(ssl, request, strlen(request));
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