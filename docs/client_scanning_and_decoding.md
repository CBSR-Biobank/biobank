# Client scanning and decoding

The client has the capability to decode [2D Data Matrix](http://en.wikipedia.org/wiki/Data_Matrix)
barcodes located on specimen tubes. The barcodes on many tubes can be decoded at once when they are
placed in a [microwell plate](http://en.wikipedia.org/wiki/Microwell_plate).  Images of microwell
plates captured using a camera or flatbed scanner can be decoded by the client.

## <a name="flatbed_scanner_configuration">Flatbed scanner configuration</a>

Before a flatbed scanner can be used with the client, configuration is required. Please see the
following link for the instructions:

* [Configuration](client_scanning_and_decoding_configuration.md)

## Decoding images

In the client, decoding of microwell plate images is possible in the following scenarios:

1. When linking specimens to patients: accessed by selecting **Processing -> Specimen Link** from the
   main menu when in the processing view (to change to this view select **View -> Open Processing
   View**).

1. When assigning storage locations to specimens: accessed by selecting **Processing -> Specimen
   Assign** from the main menu when in the processing view.

1. When creating dispatches : accessed by selecting **Processing -> Add Dispatch** from the main
   menu when in the processing view.

1. When processing received dispatches : accessed by selecting a **Receiving** dispatch from the
   **Specimen Transit** tab in the processing view.

1. When testing image decoding: accessed by selecting **Scanning and Decoding -> Decode Image** from
   the main menu.

## Decode image dialog

Decoding a microwell plate is done using the **Decode Image Dialog** which is shown
in the figure shown below.

![Decode Image Dialog](images/decode_image_dialog_plate1.png?raw=true "Decode Image Dialog")

_In the figure shown above, two regions on the flatbed scanner have been configured: one is named
**8x12** and the other **12x8**._

The dialog allows the user to select different settings on the left hand side, and displays
an image on the right. The settings are select as follows:

<table>
  <tr>
    <th width="25%">Setting</th>
    <th>Description</th>
  </tr>
  <tr>
    <td valign="top">Image Source</td>
    <td>
	  Where the image will be acquired or loaded from. Possible selections here are: one for each
      region defined with <a href="#flatbed_scanner_configuration">flatbed scanner configuration</a>
      or and image on the file system (hard disk or network location).
    </td>
  </tr>
  <tr>
    <td valign="top">Plate dimensions</td>
    <td>
	  The dimensions of the microwell plate. Valid selections are: 8x12 (eight rows by 12 columns),
      10x10, 12x12, and 9x9.
    </td>
  </tr>
  <tr>
    <td valign="top">Pallet orientation</td>
    <td>
	  The orientation of the microwell plate: either landscape or portrait. Landscape mode means
      that the row count (horizontal) is less than the column count (vertical). The figure below
      shows two plates, the one on the left is in landscape orientation and the one on the right
	  is in portrait orientation. Note that if a plate is in portrait orientation, 8x12 should
	  be selected as the dimensions.
    </td>
  </tr>
  <tr>
    <td valign="top">Barcode position</td>
    <td>
	  Where on the tubes the 2D barcodes are located. Usually, the 2D barcodes are on the bottom of
	  the tubes. However, sometimes, some images may have them on the tops of the tubes.
    </td>
  </tr>
  <tr>
    <td valign="top">DPI</td>
    <td>
	  When the image source is a flatbed scanner, this setting will be displayed, and allows the
      user to change the DPI (<i>dots per inch</i>) the image will be scanned at. This setting is
      not displayed if the image source is a file on the file system.
    </td>
  </tr>
</table>

![Landscape and portrait](images/flatbed_landscape_portrait.jpg?raw=true "Landscape and portrait")

Each tube has a **position label** that identifies the position of a single tube in the microwell
plate. A row uses an upper case letter starting at **A**, and a column uses a number starting at
**1**. The fist tube has **A1** as a position label. On a 96 well micro plate the last tube has
**H12** as a position label.

It is important that the orientation and barcode position settings are correct since it is used by
the software to determine where the first cell, **A1**, is located.


### Decoding image from flatbed scanner

Once the image source and the settings are selected, you can press the **Scan** button to acquire an
image with the flatbed scanner. Acquiring an image will take a few seconds to complete.

![Scanned plate 1](images/decode_image_dialog_plate1_image.png?raw=true "Scanned plate 1")

The scanned image is now displayed in the dialog box as shown in the figure above. Superimposed on
the image, is a grid with the number of cells that corresponds to the dimensions selected in **Plate
dimensions**. Each cell of the grid represents a well in the plate. The well may or may not have a
tube present in it. If a tube is present in the well it will be decoded. If a tube is not present
then nothing will be decoded. It is important that each cell contains the entire barcode so that the
barcode is decoded properly.

The user can use the mouse and keyboard to adjust the size of the grid.  See
section [Keyboard and mouse actions](#keyboard_and_mouse) for instructions on how to manipulate the
grid and the scanned image.

The highlighted cell, shown in cyan, is a visual cue for the position of the first cell. Hovering
with the mouse over a cell displays the position label for that cell.

Below the scanned image, the time and date the scan happened at is displayed.

Once the cells are aligned correctly the user can press the **Decode** button to decode the contents
of each barcode. Decoding the barcodes will take a few seconds. The figure shown below shows the
dialog box after decoding has completed.

![Decoded plate 1](images/decode_image_dialog_plate1_decoded.png?raw=true "Decoded plate 1")

Due to ice build up or dust on the flatbed scanner it is possible that some of the barcodes are not
decoded. Each cell that was correctly decoded has a green check box displayed in the top left. The
text below the image is updated to state the total number of tubes decoded. Hovering over a
cell now displays the position label along with the text contained on the barcode.

If a cell was not decoded the following options are available:

1. Press the **Scan** button and then the **Decode** button to start from scratch. This may be done
   if the scanned image is obscured by dirt or ice buildup on the tubes. In this case the history of
   what was decoded is lost.

1. Press the **Rescan** button and then the **Decode** button. This will aggregate the new decoded
   information with the previous information.

1. Resize the grid and press the **Decode** button. Sometimes, missed cells may now be decoded.

1. Change the DPI setting, press the **Rescan** button and then the **Decode** button. This will
   also aggregate the new decoded information with the previous information.

1. Use a hand held scanner to decode the missed cells. This can only be done after accepting the
   current decode results and pressing the **Done** button.

Once you are happy with the results of the decoding (you may still have missed cells) you can press
the **Done** button.

### Decode image in portrait orientation

As an additional example, the following figure shows the decoded tubes from a scanning region
configured as portrait:

![Decoded 8x12 plate in portrait](images/decode_image_dialog_plate2_decoded.png?raw=true "Decoded
 8x12 plate in portrait")

Tube **A1** is at the top left and tube **H1** is at the top right (as shown by where the mouse is
hovering).

### Decoding image from the file system

Decoding an image already on you file system is very similar to using a flatbed scanner. There are
less settings when doing this and are shown in the image below.

![Decode image from filesystem](images/decode_image_dialog_file.png?raw=true "Decode image from
 filesystem")

_Note that the DPI setting is no longer shown and the **Scan** and **Rescan** buttons are replaced
with the **Open File** button._

Pressing the **Open File** button allows you to select a file from the file system. On Microsoft
Windows 7 the dialog looks as follows:

![File open dialog](images/file_open_dialog.png?raw=true "File open dialog")

With this dialog you can open files with extensions: `jpg`, `jpeg`, `png` and `bmp`. After selecting
a file, it displayed in the dialog box and can then be decoded. The following figure shows an image
of a decoded 9x9 plate.

![Decoded 9x9 plate](images/decode_image_dialog_file_decoded.png?raw=true "Decoded 9x9 plate")

Note that in this image the barcodes are present on the tops of the tubes and the setting selected
for **Barcode positions** is **Tube Tops**. Cell **A1** is at the top left.

## <a name="keboard_and_mouse">Keyboard and mouse actions</a>

Here is a list of keyboard keys that can be used to manipulate the image and the grid.

<table>
  <tr>
    <th width="35%">Key(s)</th>
    <th>Description</th>
  </tr>
  <tr>
    <td valign="top">Direction keys: up, down, left, right</td>
    <td valign="top">
      Moves the grid in the corresponding direction by one pixel. Hold down to repeat.
    </td>
  </tr>
</table>

Here is a list of mouse actions that can be used to manipulate the image and the grid.

<table>
  <tr>
    <th width="35%">Action</th>
    <th>Description</th>
  </tr>
  <tr>
    <td valign="top">Left click and drag, outside grid</td>
    <td>
      Scrolls the image.
    </td>
  </tr>
  <tr>
    <td valign="top">Left click and drag, inside grid</td>
    <td>
      Moves the grid.
    </td>
  </tr>
  <tr>
    <td valign="top">Left click on resize handle and drag</td>
    <td>
      Resizes the entire grid. Note that there are eight resize handles on the region, one on each
      corner, and one on each edge midpoint.
    </td>
  </tr>
  <tr>
    <td valign="top">Hold <code>Ctrl</code> key and move mouse wheel.</td>
    <td>
      Zooms into or out of the image.
    </td>
  </tr>
</table>

****
[Back to top](../README.md)
