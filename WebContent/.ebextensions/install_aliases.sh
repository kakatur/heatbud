if [ "`grep tomcat8 /root/.bash_profile | wc -l`" == 0 ];
then
 echo "alias log=\"cd /var/log/tomcat8\"" > /tmp/tmp.txt
 echo "alias app=\"cd /var/lib/tomcat8/webapps/ROOT\"" >> /tmp/tmp.txt
 echo "alias jsp=\"cd /var/lib/tomcat8/webapps/ROOT/WEB-INF/jsp\"" >> /tmp/tmp.txt
 cat /tmp/tmp.txt >> /root/.bash_profile;
fi
