if [ -f "/swapfile" ];
then
 /sbin/swapon -s
else
 dd if=/dev/zero of=/swapfile bs=1024 count=1048576
 /sbin/mkswap /swapfile
 /sbin/swapon /swapfile
 /sbin/swapon -s
fi
