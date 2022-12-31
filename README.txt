BinaryIn.java, BinaryOut.java, and Huffman.java are all downloaded from https://algs4.cs.princeton.edu/code/edu/princeton/cs/algs4/. 
All of my original contributions are in HuffmanSubmit.java.

-----------------------------------------

Notes while testing:
- It seems like encode() and frequency files work correctly for both alice30.txt and ur.jpg
- However, decode() works correctly for alice30.txt but not for ur.jpg 
(ur_dec.jpg is generated midway until it encounters an error, so half of the image is decoded correctly)
I tried various other texts and (low quality) images. In every cases, the texts are decoded correctly, but the images are all half-decoded.
I do not know how to resolve this problem, though.
- Otherwise there is no other notable problems. encode() and decode() can work independently in the main method.