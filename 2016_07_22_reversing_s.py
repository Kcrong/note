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
sock.send("123 456 789\n")

sock.close()
