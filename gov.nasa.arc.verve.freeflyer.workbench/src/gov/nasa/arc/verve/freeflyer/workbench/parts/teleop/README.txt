Explanation of TeleopCommandConfigs:

They all map to a subclass of AbstractTeleopCommandConfig.  So we have:

"CameraPresetTeleopCommand"
- has object OptionsForOneCamera, which lists a camera name and then the presets for that camera.
- makes a widget with one dropdown for camera, then another dropdown for the preset
- customize by adding or removing OptionsForOneCamera objects

"DataManagementTeleopCommand"
- for sending Start Download, Stop Download, and Clear Data commands for Immediate or Delayed data
- makes a widget where you select the DataType, and then the Action
- customize by adding types of Data
- customize by adding other commands that can be performed on that data (also add new command names)

"FlashlightTeleopCommand"
- for sending setFlashlightBrightness command, user selects name instead of a number
- creates widget with dropdown for flashlight name, and another dropdown for brightness level
- customize by adding flashlight names
- customize by adding named brightness levels (add to both name and value arrays)

"HorizontalSeparatorTeleopCommand"
- for separating groups of commands in the interface
- creates a horizontal line
- can't be customized

"NoParamsTeleopCommand"
- for sending a single command that takes no parameters
- creates a row with the command name, and button that takes up two-thirds
- customize by specifying the command, subsystem, label, button text

"OperatingLimitsTeleopCommand"
- for sending a set of operating limits
- creates a dropdown with the list of names of sets of operating limits
- reads the operating limits options from OperatingLimitsConfigurations.json (advanced tab widget reads same file)
- customize by adding operating limits to OperatingLimitsConfigurations.json

"OppositeCommandsTeleopCommand"
- for sending powerOnItem and powerOffItem (commands that are opposites, and take on string)
- creates a label with a toggle button
- customize by changing paramValue (and the label) to change what is being turned on and off
- could be customized to work with another pair of opposite commands that take a string

"StringBooleanTeleopCommand"
- for sending a command that has param string (from known set), and a boolean (setCameraStreaming, setCameraRecording)
- creates a dropdown for the first string, and a dropdown with True/False for the boolean
- customize by adding different camera names
- customize by changing command name, and names of the parameters

"StringListIntTeleopCommand" (reserve, might not work)
- for sending command with an int that is represented to the user as a string (manual dock)
- creates a dropdown with list of paramOptions (that will be converted to int)
- customize by changing command/param names, and the list of options

"StringListTeleopCommand" (reserve, might not work)
- for sending a command that has one String (from a set) (ie clearData)
- creates dropdown with list of paramOptions
- customize by changing command and list of options

"ToggleBooleanTeleopCommand" (reserve)
- for sending a command that has a boolean parameter (enableAutoreturn)
- creates label and button that is two-thirds of the column
- customize by changing name of command, and what the labels on the buttons are

"TwoFloatsTeleopCommand" (reserve)
- for sending a command that takes two floats (armPanAndTilt)
- creates two lines, 1st line has label, IncrementableText, and button. 2nd line has label and IncrementableText
- customize by changing the names and ranges of the floats, and the command name



