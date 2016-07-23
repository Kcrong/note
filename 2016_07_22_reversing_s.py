from socket import *
from time import sleep

sock = socket(AF_INET, SOCK_STREAM)
#sock.connect(('192.168.32.188', 8282))
sock.connect(('127.0.0.1', 8282))

sleep(0.1)
sock.recv(1024)
sock.send("test\n")

sleep(0.1)
sock.recv(1024)
sock.send("-1742237696 7368 -1344441807\n")

sock.close()

'''
ebp+8 90000000
ebp+c 8278cc8
ebp+10

eax = ebp+8 ^ ebp+c
eax == 98278CC8
ecx = ebp+c ^ ebp+10
ecx == 0AFDD6EF9
edx = ebp+8 + ebp+10
edx == 48050231

a ^ b = alpha
b ^ c = beta
c + a = theta

a = alpha ^ b
a = theta - c
c = beta ^ b
a = theta - beta ^ b

alpha ^ b = theta - beta ^ b
b = (theta - beta ^ b) ^ alpha;

b = 0x1CC8
a = 0x98279000
c = afdd7231

'''
