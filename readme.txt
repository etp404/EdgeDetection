Instructions for creating appropriate OpenCV libraries are here: http://docs.opencv.org/2.4.4-beta/doc/tutorials/introduction/desktop_java/java_dev_intro.html

Add -Djava.library.path=.../opencv/build/lib to VM options.

Afer a Ubuntu update, I received the messages saying that cmake couldn't find the JNI.

I resolved this by running with the following options.
cmake -DBUILD_SHARED_LIBS=OFF -DJAVA_INCLUDE_PATH=/usr/lib/jvm/java-6-openjdk-amd64/include -DJAVA_INCLUDE_PATH2=/usr/lib/jvm/java-6-openjdk-amd64/include/linux  ..


Summary for Confluence
As part of a hack day project we looked into automatic selection of the region of an image to show next to a text story. Blind cropping of the image can mean that semantically important image regions can be obscured, so we wanted to select regions of visual interest and sacrifice those that were less important.

Although face detection initially appears to be a promising direction, the concern was that because most face detectors are trained on sample images then some categories of face that had not been sufficiently represented in the training set could be ignored. There is also the issue that photographs of people playing sport are likely to show a range of facial grimaces or wear equipment that might not be present in the training set.

Instead we looked at the concept of low-level visual saliency (see the work of Itti, Koch et al., one of the first groups of people to attempt to create a saliency map of a natural image). One of the issues that vision scientists battle against is that of artistic bias. A photographer will tend to focus sharply on areas of high-level (semantic) interest, meaning that it is difficult in psychophysical studies to determine whether high- or low-level cues are attracting gaze. In the hack day project this factor actually becomes very useful. We don't care whether it is a high-level psychological interest in faces or objects or a low-level physiological bias towards 'salient' regions that makes an area interesting, so  we can benefit from artistic bias. The interesting area will be likely to have lots of 'stuff' going on, and is also likely to be in sharp focus because a professional photographer will decides which area he or she wants the viewer to look at. 

We therefore decided to detect high-frequency (sharp) edges in the image, and select the region which had the highest density of such edges. The edges were extracted by convolving the luminance channels of the images with a Gabor filter, using a phase of pi/2 (to detect edges vs. blobs) and at eight different orientations and at carrier wavelengths of 1, 3 and 5 pixels. For each pixel, the maximum response from the eight orientations was taken to represent edge density for that pixel at each scale, and the response was summed over scales. The optimal region was selected by brute force, since there was only one degree of freedom: the horizontal axis. 

Results are attached as a zip file. For a 'bad' example in which the algorithm fails, see 176549562_withHeadline.png.

