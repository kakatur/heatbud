echo "# minute          0-5" > /tmp/tmp.txt
echo "# hour            0-23" >> /tmp/tmp.txt
echo "# day of month    1-31" >> /tmp/tmp.txt
echo "# month           1-12" >> /tmp/tmp.txt
echo "# day of week     0-6, with 0=Sunday" >> /tmp/tmp.txt
export rand=`hostname | cut -d"-" -f5`
export hours=`echo $((rand%24))`
export minutes=`echo $((rand%60))`
echo "$minutes  $hours * * 0 \$HOME/backup_s3.sh > /tmp/backup_s3.out 2>&1" >> /tmp/tmp.txt
crontab /tmp/tmp.txt
rm -f /tmp/tmp.txt
