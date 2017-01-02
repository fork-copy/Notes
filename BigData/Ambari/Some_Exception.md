node1 down info:
```
ABRT has detected 5 problem(s). For more info run: abrt-cli list --since 1482719058
[root@node1 ~]# abrt-cli list --since 1482719058
id 899a457856e811867d0c799c2d1039ada8bd30c2
time:           Wed 28 Dec 2016 11:40:43 PM CST
uid:            0 (root)
count:          1
Directory:      /var/spool/abrt/vmcore-127.0.0.1-2016-12-28-23:39:44

id 50d298da43547f4fa388356fa0007bfb70d971b3
time:           Wed 28 Dec 2016 11:40:40 PM CST
uid:            0 (root)
count:          1
Directory:      /var/spool/abrt/vmcore-127.0.0.1-2016-12-17-00:26:54

id 6f08593d2b365239cdc22ecdc0a61a3e18152345
time:           Wed 28 Dec 2016 11:40:37 PM CST
uid:            0 (root)
count:          1
Directory:      /var/spool/abrt/vmcore-127.0.0.1-2016-12-03-00:23:29

id b0c884c64fe3723860eebd7d81c381276eb4a5f4
time:           Wed 28 Dec 2016 11:40:36 PM CST
uid:            0 (root)
count:          1
Directory:      /var/spool/abrt/vmcore-127.0.0.1-2016-11-24-09:49:33

id 5b1bb2066ba57f3f3666c8e89e612a9e8dd2c88b
reason:         gnome-shell killed by SIGSEGV
time:           Wed 28 Dec 2016 10:50:02 PM CST
cmdline:        gnome-shell --mode=gdm
package:        gnome-shell-3.14.4-37.el7
uid:            42 (gdm)
count:          1
Directory:      /var/spool/abrt/ccpp-2016-12-28-22:50:02-2526


The Autoreporting feature is disabled. Please consider enabling it by issuing
'abrt-auto-reporting enabled' as a user with root privileges
```