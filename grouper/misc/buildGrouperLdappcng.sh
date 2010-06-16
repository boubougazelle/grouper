#!/bin/bash

if [ $# -ne "1" ]
then
  echo
  echo "Give the version to build as the command line argument!"
  echo "e.g. HEAD, GROUPER_1_3_1, etc"
  echo "e.g. buildGrouper.sh HEAD"
  echo
  exit 1
fi  

HOME=/home/mchyzer

DIR=$HOME/tmp/ldappcng

cd $HOME/tmp
if [ ! -d $DIR ]; then
  /bin/mkdir $DIR
  /bin/chmod g+w $DIR
fi

cd $DIR

export buildDir=$DIR/build_$USER

if [ -d $buildDir ]; then
  /bin/rm -rf $buildDir
fi

if [ ! -d $buildDir ]; then
  /bin/mkdir $buildDir
fi

cd $buildDir

/usr/bin/svn export https://svn.internet2.edu/svn/i2mi/tags/$1/ldappcng/ldappcng

cd $buildDir/ldappcng

$M2_HOME/bin/mvn package -DskipTests

cd $HOME/tmp/grouper/build_$USER/grouper

ln -s $buildDir/ldappcng ../ldappcng_trunk

$ANT_HOME/bin/ant ldappcng

mv $HOME/tmp/grouper/build_$USER/grouper/dist/ldappcng/grouper.ldappcng-*.tar.gz $HOME/tmp/grouper/build_$USER

echo
echo "regular result is in $buildDir/" 
echo "binary result is in $buildDir/" 
echo "grouper binary is in $HOME/tmp/grouper/build_$USER/"
echo

#allow someone from group to delete later on
/bin/chmod -R g+w $buildDir
