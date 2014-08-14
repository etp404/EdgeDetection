Instructions for creating appropriate OpenCV libraries are here: http://docs.opencv.org/2.4.4-beta/doc/tutorials/introduction/desktop_java/java_dev_intro.html

Add -Djava.library.path=.../opencv/build/lib to VM options.

Afer a Ubuntu update, when running can't find JNI I received the messages saying that cmake couldn't find the JNI.

I resolved this by running with the following options.
cmake -DBUILD_SHARED_LIBS=OFF -DJAVA_INCLUDE_PATH=/usr/lib/jvm/java-6-openjdk-amd64/include -DJAVA_INCLUDE_PATH2=/usr/lib/jvm/java-6-openjdk-amd64/include/linux  ..