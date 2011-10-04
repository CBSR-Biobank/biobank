;NSIS Modern User Interface version 1.69
;Original templates by Joost Verburg
;Redesigned for BZFlag by blast007
;Redesigned for BioBank by Thomas Polasek

;--------------------------------
;BioBank Version Variables

!define PRODUCT_NAME "BioBank"
  !define VERSION_STR "2.0.1.a" 
  !define EXPORTED_PRODUCT_NAME "${PRODUCT_NAME}_v${VERSION_STR}_win32"

; if JAVA is to be installed the createInstaller.pl perl script
; will uncomment this line
;!define INSTALL_JAVA

; Definitions for Java 6.0
!define JRE_VERSION "6.0"
!define JRE_URL "http://javadl.sun.com/webapps/download/AutoDL?BundleId=42742&/jre-6u22-windows-i586-p.exe"
 
; use javaw.exe to avoid dosbox.
; use java.exe to keep stdout/stderr
!define JAVAEXE "javaw.exe"

!include "FileFunc.nsh"
!insertmacro GetFileVersion
!insertmacro GetParameters
!include "WordFunc.nsh"
!insertmacro VersionCompare
!include LogicLib.nsh

;--------------------------------
;Compression options

  ;If you want to comment these
  ;out while testing, it speeds
  ;up the installer compile time
  ;Uncomment to reduce installer
  ;size by ~35%
  
  ;SetCompress off
  SetCompress auto
  SetCompressor /SOLID lzma

;--------------------------------
;Include Modern UI

  !include "MUI.nsh"

;--------------------------------
;Configuration

  ;General
  Name "${PRODUCT_NAME} ${VERSION_STR}"
  OutFile "..\${PRODUCT_NAME}Installer-${VERSION_STR}.exe"

  ;Default installation folder
  InstallDir "$PROGRAMFILES\${PRODUCT_NAME}"

  ; Make it look pretty in XP
  XPStyle on

;--------------------------------
;Variables
  Var STARTMENU_STR
  Var STARTMENU_FOLDER
  Var INSTALL_DIR_STR
  
  

;--------------------------------
;Interface Settings

  ;Icons
  !define MUI_ICON "biobank.ico"
  !define MUI_UNICON "uninstall.ico"

  ;Bitmaps
  !define MUI_WELCOMEFINISHPAGE_BITMAP "side.bmp"
  !define MUI_UNWELCOMEFINISHPAGE_BITMAP "side.bmp"

  !define MUI_HEADERIMAGE
  !define MUI_HEADERIMAGE_BITMAP "header.bmp"
  !define MUI_COMPONENTSPAGE_CHECKBITMAP "${NSISDIR}\Contrib\Graphics\Checks\simple-round2.bmp"

  !define MUI_COMPONENTSPAGE_SMALLDESC

  ;Show a warning before aborting install
  !define MUI_ABORTWARNING

;--------------------------------
;Pages

  ;Welcome page configuration
  !define MUI_WELCOMEPAGE_TEXT "This wizard will guide you through the installation of ${PRODUCT_NAME} ${VERSION_STR}.\r\n\r\nThe ${PRODUCT_NAME} Java Client is an application that connects to the ${PRODUCT_NAME} server and allows BioSample repository technicians to manipulate the inventory information for their BioSample repository.\r\n\r\nClick Next to continue."

  
  !insertmacro MUI_PAGE_WELCOME
  !insertmacro MUI_PAGE_LICENSE "licence.rtf"
  !insertmacro MUI_PAGE_COMPONENTS
  !insertmacro MUI_PAGE_DIRECTORY

  ;Start Menu Folder Page Configuration
  !define MUI_STARTMENUPAGE_REGISTRY_ROOT "HKLM" 
  !define MUI_STARTMENUPAGE_REGISTRY_KEY "Software\${PRODUCT_NAME}" 
  !define MUI_STARTMENUPAGE_REGISTRY_VALUENAME "Start Menu Folder"
  !define MUI_STARTMENUPAGE_DEFAULTFOLDER "${PRODUCT_NAME}"

  !insertmacro MUI_PAGE_STARTMENU Application $STARTMENU_FOLDER

  !insertmacro MUI_PAGE_INSTFILES
  
  ;Finished page configuration
  !define MUI_FINISHPAGE_NOAUTOCLOSE
  !define MUI_FINISHPAGE_LINK "http://aicml-med.cs.ualberta.ca/redmine/projects/${PRODUCT_NAME}/wiki"
  !define MUI_FINISHPAGE_LINK_LOCATION "http://aicml-med.cs.ualberta.ca/redmine/projects/${PRODUCT_NAME}/wiki"
  

  !insertmacro MUI_PAGE_FINISH
  
  !insertmacro MUI_UNPAGE_WELCOME
  !insertmacro MUI_UNPAGE_CONFIRM
  !insertmacro MUI_UNPAGE_INSTFILES

  !define MUI_UNFINISHPAGE_NOAUTOCLOSE
  !insertmacro MUI_UNPAGE_FINISH
  
;--------------------------------
;Languages
 
  !insertmacro MUI_LANGUAGE "English"

!macro MACRO_UNINSTALL
  ; remove the directory we will be installing to. (if it exists)
  RMDir /R "$INSTDIR"

  ; remove the previous installation
  ReadRegStr $INSTALL_DIR_STR HKLM SOFTWARE\${PRODUCT_NAME} "Install_Dir"
  IfErrors +2 0
  RMDir /R "$INSTALL_DIR_STR"

  ;delete startmenu (directory stored in registry)
  ReadRegStr $STARTMENU_STR HKLM SOFTWARE\${PRODUCT_NAME} "Start Menu Folder"
  IfErrors +2 0
  RMDir /R "$SMPROGRAMS\$STARTMENU_STR"
  
  ;delete quicklaunch, desktop
  Delete "$QUICKLAUNCH\${PRODUCT_NAME}.lnk"
  Delete "$DESKTOP\${PRODUCT_NAME}.lnk"
  
   ;remove registry keys
  DeleteRegKey HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\${PRODUCT_NAME}"
  DeleteRegKey HKLM "SOFTWARE\${PRODUCT_NAME}"
  DeleteRegKey HKCU "Software\${PRODUCT_NAME}"
!macroend


RequestExecutionLevel admin ;Require admin rights on NT6+ (When UAC is turned on)

Function .onInit
UserInfo::GetAccountType
pop $0
${If} $0 != "admin" ;Require admin rights on NT4+
    MessageBox mb_iconstop "Administrator rights required!"
    SetErrorLevel 740 ;ERROR_ELEVATION_REQUIRED
    Quit
${EndIf}
FunctionEnd

;--------------------------------
;Installer Sections

Section "!BioBank Core(Required)" BioBank
  ;Make it required
  SectionIn RO
  
  IfFileExists "$QUICKLAUNCH\BioBank.lnk" 0 CHECK_DESKTOP_SHORTCUT
  Delete "$QUICKLAUNCH\BioBank.lnk"  
  
CHECK_DESKTOP_SHORTCUT:  
  IfFileExists "$DESKTOP\BioBank.lnk" 0 CHECK_PREV_INSTALL
  Delete "$DESKTOP\BioBank.lnk"
  
CHECK_PREV_INSTALL:  
  ;If we find the BioBank key.. then uninstall the previous version of BioBank
  ClearErrors
  ReadRegStr $0 HKLM Software\BioBank "BioBank"
  IfErrors 0 PREV_INSTALL 
  
  StrCpy $0 "${PRODUCT_NAME}"
  ReadRegStr $0 HKLM SOFTWARE\${PRODUCT_NAME} $0
  IfErrors NOT_PREV_INSTALLED 0

  ;Biobank is installed from nsis, remove it.
PREV_INSTALL:  
  !insertmacro MACRO_UNINSTALL
  goto INSTALL_BIOBANK_CORE    
 
NOT_PREV_INSTALLED:
  ;Move settings to the new location. (Please note that the \ must at the end of the second but not first directory)
  IfFileExists "C:\Program Files\${PRODUCT_NAME}\${PRODUCT_NAME}_v1.2.0_win32\workspace" 0 INSTALL_BIOBANK_CORE
  Exec '"xcopy.exe" "C:\Program Files\${PRODUCT_NAME}\${PRODUCT_NAME}_v1.2.0_win32\workspace" "$PROFILE\${PRODUCT_NAME}\" /S /Y /E'

INSTALL_BIOBANK_CORE:
!ifdef INSTALL_JAVA  
  Call GetJRE
!endif  
  ; copy over the exported biobank directory
  SetOutPath $INSTDIR\${EXPORTED_PRODUCT_NAME}
  File /r ..\${EXPORTED_PRODUCT_NAME}\*
  
  ; make the doc dir
  SetOutPath $INSTDIR\${EXPORTED_PRODUCT_NAME}\doc
  File licence.rtf
  
  ; remove registry keys with bad product name
  DeleteRegKey HKLM SOFTWARE\BioBank
  DeleteRegKey HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\BioBank"
  
  ;Write biobank registry keys
  WriteRegStr HKLM SOFTWARE\${PRODUCT_NAME} "Version" "${VERSION_STR}"
  WriteRegStr HKLM SOFTWARE\${PRODUCT_NAME} "Install_Dir" "$INSTDIR"
  WriteRegStr HKLM SOFTWARE\${PRODUCT_NAME} "${PRODUCT_NAME}" "I do not fear computers. I fear the lack of them."
  
  
  ; Write the uninstall keys for Windows
  WriteRegStr HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\${PRODUCT_NAME}" "DisplayName" "${PRODUCT_NAME} (remove only)"
  WriteRegStr HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\${PRODUCT_NAME}" "UninstallString" '"$INSTDIR\${EXPORTED_PRODUCT_NAME}\uninstall.exe"'
  
  ;Create uninstaller
  WriteUninstaller "$INSTDIR\${EXPORTED_PRODUCT_NAME}\Uninstall.exe"

  !insertmacro MUI_STARTMENU_WRITE_BEGIN Application
    
    ;Main start menu shortcuts
    SetOutPath $INSTDIR\${EXPORTED_PRODUCT_NAME}
    CreateDirectory "$SMPROGRAMS\$STARTMENU_FOLDER"
    CreateShortCut "$SMPROGRAMS\$STARTMENU_FOLDER\Uninstall.lnk" "$INSTDIR\${EXPORTED_PRODUCT_NAME}\uninstall.exe" "" "$INSTDIR\${EXPORTED_PRODUCT_NAME}\uninstall.exe" 0
    CreateShortCut "$SMPROGRAMS\$STARTMENU_FOLDER\${PRODUCT_NAME}.lnk" "$INSTDIR\${EXPORTED_PRODUCT_NAME}\${PRODUCT_NAME}.exe" "" "$INSTDIR\${EXPORTED_PRODUCT_NAME}\${PRODUCT_NAME}.exe" 0
  !insertmacro MUI_STARTMENU_WRITE_END

SectionEnd

Section "Quick Launch Shortcuts" QuickLaunch
  ;shortcut in the "quick launch bar"
  SetOutPath $INSTDIR\${EXPORTED_PRODUCT_NAME}
  CreateShortCut "$QUICKLAUNCH\${PRODUCT_NAME}.lnk" "$INSTDIR\${EXPORTED_PRODUCT_NAME}\${PRODUCT_NAME}.exe" "" "$INSTDIR\${EXPORTED_PRODUCT_NAME}\${PRODUCT_NAME}.exe" 0
SectionEnd

Section "Desktop Icon" Desktop
  ;shortcut on the "desktop"
  SetOutPath $INSTDIR\${EXPORTED_PRODUCT_NAME}
  CreateShortCut "$DESKTOP\${PRODUCT_NAME}.lnk" "$INSTDIR\${EXPORTED_PRODUCT_NAME}\${PRODUCT_NAME}.exe" "" "$INSTDIR\${EXPORTED_PRODUCT_NAME}\${PRODUCT_NAME}.exe" 0
SectionEnd

;--------------------------------
;Descriptions

  ;Assign language strings to sections
  !insertmacro MUI_FUNCTION_DESCRIPTION_BEGIN
    !insertmacro MUI_DESCRIPTION_TEXT ${Biobank} "Install the main BioBank application"
    !insertmacro MUI_DESCRIPTION_TEXT ${QuickLaunch} "Adds a shortcut in the Quick Launch toolbar."
    !insertmacro MUI_DESCRIPTION_TEXT ${Desktop} "Adds a shortcut on the desktop."
  !insertmacro MUI_FUNCTION_DESCRIPTION_END

;--------------------------------


Section "Uninstall"
   !insertmacro MACRO_UNINSTALL
SectionEnd

;  returns the full path of a valid java.exe
;  looks in:
;  1 - .\jre directory (JRE Installed with application)
;  2 - JAVA_HOME environment variable
;  3 - the registry
;  4 - hopes it is in current dir or PATH
Function GetJRE
    Push $R0
    Push $R1
    Push $2
 
  ; 1) Check local JRE
  CheckLocal:
    ClearErrors
    StrCpy $R0 "$EXEDIR\jre\bin\${JAVAEXE}"
    IfFileExists $R0 JreFound
 
  ; 2) Check for JAVA_HOME
  CheckJavaHome:
    ClearErrors
    ReadEnvStr $R0 "JAVA_HOME"
    StrCpy $R0 "$R0\bin\${JAVAEXE}"
    IfErrors CheckRegistry     
    IfFileExists $R0 0 CheckRegistry
    Call CheckJREVersion
    IfErrors CheckRegistry JreFound
 
  ; 3) Check for registry
  CheckRegistry:
    ClearErrors
    ReadRegStr $R1 HKLM "SOFTWARE\JavaSoft\Java Runtime Environment" "CurrentVersion"
    ReadRegStr $R0 HKLM "SOFTWARE\JavaSoft\Java Runtime Environment\$R1" "JavaHome"
    StrCpy $R0 "$R0\bin\${JAVAEXE}"
    IfErrors DownloadJRE
    IfFileExists $R0 0 DownloadJRE
    Call CheckJREVersion
    IfErrors DownloadJRE JreFound
 
  DownloadJRE:
    MessageBox MB_ICONINFORMATION "${PRODUCT_NAME} uses Java Runtime Environment ${JRE_VERSION}, it will now be downloaded and installed."
    StrCpy $2 "$TEMP\Java Runtime Environment.exe"
    nsisdl::download /TIMEOUT=30000 ${JRE_URL} $2
    Pop $R0 ;Get the return value
    StrCmp $R0 "success" +3
      MessageBox MB_ICONSTOP "Download failed: $R0"
      Abort
    ExecWait $2
    Delete $2
 
    ReadRegStr $R1 HKLM "SOFTWARE\JavaSoft\Java Runtime Environment" "CurrentVersion"
    ReadRegStr $R0 HKLM "SOFTWARE\JavaSoft\Java Runtime Environment\$R1" "JavaHome"
    StrCpy $R0 "$R0\bin\${JAVAEXE}"
    IfFileExists $R0 0 GoodLuck
    Call CheckJREVersion
    IfErrors GoodLuck JreFound
 
  ; 4) wishing you good luck
  GoodLuck:
    StrCpy $R0 "${JAVAEXE}"
    ; MessageBox MB_ICONSTOP "Cannot find appropriate Java Runtime Environment."
    ; Abort
 
  JreFound:
    Pop $2
    Pop $R1
    Exch $R0
FunctionEnd
 
; Pass the "javaw.exe" path by $R0
Function CheckJREVersion
    Push $R1
 
    ; Get the file version of javaw.exe
    ${GetFileVersion} $R0 $R1
    ${VersionCompare} ${JRE_VERSION} $R1 $R1
 
    ; Check whether $R1 != "1"
    ClearErrors
    StrCmp $R1 "1" 0 CheckDone
    SetErrors
 
  CheckDone:
    Pop $R1
FunctionEnd

