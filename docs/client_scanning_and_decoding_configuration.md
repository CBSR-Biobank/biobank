# Client scanning and decoding configuration

The client has the capability of decoding [2D Data Matrix](http://en.wikipedia.org/wiki/Data_Matrix)
barcodes on located on specimen tubes. The barcodes of multiple tubes can be decoded when they
placed in a [microwell plate](http://en.wikipedia.org/wiki/Microwell_plate). Follow the instructions
given below to configure the client to use a flatbed scanner to decode the barcodes on these types
of plates.

## Scanning and decoding

From the client main menu, select **Configuration -> Preferences** to open the *Preferences* dialog
box. Select and expand the **Scanning and Decoding** item on the left hand side and you will see a
dialog box similar to the one shown below.  Note that you do not have to be logged into the Biobank
server to peform these steps.

![Scanning and decoding](images/prefs_scanning_and_decoding.png?raw=true "Scanning and decoding
 preferences")

To select a flatbed scanner, press the `Select Scanner` button. The figure shown below shows the
dialog box that is shown when the button is pressed.

![Scanning source](images/prefs_select_source.png?raw=true "Selecting a scanning source")

Once a scanner is selected, the selection for **Driver Type** will automatically update, but
sometimes the the correct type is not selected. Use the check boxes here to override the automatic
selection if it was incorrect.  For best results, always use the
[WIA](http://en.wikipedia.org/wiki/Windows_Image_Acquisition) driver type.

The values in **Brightness** and **Constract** allow these setting to be adjusted when an image is
scanned. However, these parameters do not work on Hewlett-Packard scanners when using the WIA based
driver, so these values can be left at zero.

## Decoding Parameters

The values for these parameters do not need to be modified by normal users, and they can be left at
their default values. If there is a problem with scanning or decoding you may be instructed to
change some of these values during technical assistance.

Select **Decoding Parameters** from the *Preferences* dialog box (note that the *Scanning and
Decoding* item needs to be expanded). The dialog now looks similar to the one shown below:

![Decoding parameters](images/prefs_decoding_params.png?raw=true "Decoding parameters")

Each setting is described below:

<table>
<tr>
<th width="25%">Setting</th>
<th>Description</th>
</tr>
<tr>
<td valign="top">Library Debug Level</td>
<td>
Used to output debugging information when scanning and decoding images. Possible values are 0
through 9. The higher the value the more detailed the debugging information. A zero values
does not generate any output.
</td>
</tr>
<tr>
<td valign="top">Edge Minimum Factor</td>
<td>
Pixel length of smallest expected edge in image  as a factor of the cell width or cell height
(whichever is bigger). The default values is 0.2.
</td>
</tr>
<td valign="top">Edge Maximum Factor  </td>
<td>
Pixel length of largest expected edge in image  as a factor of the cell width or cell height
(whichever is bigger). The default values is 0.4.
</td>
</tr>
<tr>
<td valign="top">Scan Gap Factor</td>
<td>
The scan grid gap size as a factor of the cell width or cell height (whichever is bigger). The
default value is 0.15.
</td>
</tr>
<tr>
<td valign="top">Edge Threshold</td>
<td>
Set the minimum edge threshold as a percentage of maximum.  For example, an edge between a pure
white and pure black pixel would have an intensity of 100. Edges with intensities below the
indicated threshold will be ignored by the decoding process. Lowering the threshold will increase
the amount of work to be done, but may be necessary for low contrast or blurry images. The default
and recommended value is 5.
</td>
</tr>
<tr>
<td valign="top">Square Deviation</td>
<td>
Maximum deviation (in degrees) from squareness between adjacent barcode sides. The default and
recommended value is <code>N=15</code> and is meant for scanned images. Barcode regions found with
corners <code>&lt;(90-N)</code> or <code>&gt;(90+N)</code> will be ignored by the decoder. The
default value is 15.
</td>
</tr>
<tr>
<td valign="top">Corrections</td>
<td>
The number of corrections to make while decoding. The default and recommended value is 10.
</td>
</tr>
</table>

## Plate position

Plate positions can be configured to decode different sized well plates. For example, the first
plate can be configured to scan 96 well plates, the second for 81 well boxes, etc.

Select **Plate 1 Position** from the *Preferences* dialog box (note that the *Scanning and
Decoding* item needs to be expanded). The dialog now looks similar to the one shown below:

![Defining a plate](images/plate1_definition.png?raw=true "Scanning and decoding
 preferences")

To define a plate position do the following after selecting a scanner as described above:

1. Place a pallet that contains tubes on the flatbed scanner. Ensure the top edge of the pallet is
   touching the top of the scanning region, and the right edge of the pallet is touching the right
   margin.

1. Select the plate region you are going to define.  If it is the first select **Plate 1 Position**.

1. Click on the **Enable** check box.

1. Press the **Scan** button. Now wait for the scanner to scan the entire flatbed.

    ![Defining a plate position](images/plate1_with_grid.png?raw=true "Defining a plate position")

1. Once the scan is done, you will see something similar to the figure shown above. The image shown
   is the scanned image and superimposed is a *region* shown in red. The region in red will be
   scanned when decoding a plate when processing specimens. Note that you can zoom into and out of
   the image by holding the `Ctrl` key on the keyboard and at the same time scrolling up or down on
   the mouse's wheel. After zooming in, you can drag the image by left clicking on the image
   anywhere outside the red region.

1. You can adjust the size of the region using the mouse. If the region is moved, the numbers
   displayed for *Left*, *Top*, *Right*, and *Bottom* change. You can also enter numbers into
   these values to change the position of the corresponding edge of the region.

    If you move the mouse to one of the corners, or the midpoint of one of the edges, you can resize
    the grid by left clicking on the mouse and moving it. The whole grid can be moved by pressing
    the left mouse button while hovering inside the region.

1. Once the region contains all the tubes press the **Apply** button. It is usually better to make
   the region slightly larger than the edge of the tubes so as to not cut off the barcodes.

1. Repeat from step 2 to define any more pallet scanning regions.

Usually only one pallet scanning region is required for normal operation of the software.

The figure below shows an example of how **Plate 2** can be defined. Here Plate 2 is scanned in
**Portrait** mode and is touching the top and the left margin of the of the flatbed region.

![Defining a plate position](images/plate2_with_grid.png?raw=true "Defining another plate position")

You can assign a name to each plate region so that it is easier to remember it later when used in
processing specimens. In the sample configuration described above, plate 1 was given the name
**8x12** and plate 2 **12x8**. By using different names it makes it easier to use different plate
configurations during specimen processing.

To test if your configuration yields valid decoded tubes, use **Scanning and Decoding -> Decode
Plate** from the main menu.


[Back to top](../README.md)
