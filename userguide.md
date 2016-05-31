#Launch and Initial Settings
The project is launched using either the .jar file provided or by running the main class, View.java, contained in the source code.

##Loading the .MER File
Once the project is launched for the first time, a .MER file can be selected using the "Select .MER File" button within the "Settings" tab, whcih opens an open file dialog.

###Directions for Creating a New .MER File
(taken from Mr. Estep's directions)

Open FileMaker Pro

Change Layout to HCOURSES

File...Export Records

In the Dialog box...

1. change the "Save as type" dropdown box to "Merge Files (*.mer)"

2. navigate to, and click on the file you are updating

3. click "Save"  (say yes to wanting to replace it on the pop-up dialog)
	
In the "Specify Field Order for Export" dialog...

1. make sure drop down says "Current Layout ("HCOURSES")

2. make sure "Field export order" is empty (lower right) by clicking "Clear All"

3. click "Move All" button

4. click "Export" button
	
#General Use
The "Basic Generation" tab is used for most use cases. The period spinner is used for selecting a period; it only accepts numerical data from 1-8 that is one character long.
There are four checkboxes for the grades - whatever grades are selected will be included in the generated seating chart, provided at least one is selected.
There are also two checkboxes for gender that are, by default, active. These determine which genders are accounted for in the count for the seating.
Once the settings are what are wanted by the user, the user can press the "Generate Seating Chart" button to create the seating chart and view it on the preview.

With the generated seating chart, the user can either save the PDF using the save button, or email it using the email button.
The email button additionally opens a dialog in which the user can type a message to send to the teachers who are on the seating chart.
By then pressing the "Send Email" button in the dialog, an email is sent to all relevant teachers with the specified text and the seating chart attached.

#Advanced Use
Once a period and a .MER file are selected, the user may navigate to the "Advanced Generation" tab.
This allows them to search for teachers to move to the front of the classroom.
The user may press Tab or Enter at any time to autocomplete to the nearest possible suggestion.
Once a valid teacher name is in the search bar, pressing Enter adds the teacher to the restrictions list.
This is the list of teachers to be moved to the front of the auditorium.
These restrictions can be moved either by drag-and-drop or the arrows on the restriction cell.
The restriction can be removed using the "X" button on it.

#Additional Settings
The "Settings" tab, as used previously for selected the .MER file, can also be used for customization.
If the user is not satisfied with the colors of the GUI, they can select a color using the color pickers present in the tab.
These settings, including the .MER file location, are persistent throughout multiple runs of the program.
