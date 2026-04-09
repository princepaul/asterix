/*
 *  Copyright (c) 2013 Croatia Control Ltd. (www.crocontrol.hr)
 *
 *  This file is part of Asterix.
 *
 *  Asterix is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  Asterix is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with Asterix.  If not, see <http://www.gnu.org/licenses/>.
 *
 *
 * AUTHORS: Damir Salantic, Croatia Control Ltd.
 *
 */

#include <stdio.h>
#include <string.h>
#include <unistd.h>
#include <stdarg.h>
#include <set>
#include <string>

#include "asterix.h"
#include "asterixformat.hxx"
#include "asterixrawsubformat.hxx"
#include "asterixpcapsubformat.hxx"
#include "asterixfinalsubformat.hxx"
#include "asterixformatdescriptor.hxx"
#include "asterixhdlcsubformat.hxx"
#include "asterixgpssubformat.hxx"

#include "Tracer.h"
#include "XMLParser.h"

extern int gCategory;
extern int gVersionMajor;
extern int gVersionMinor;
extern int gSelectedCatVersions[256][2];
extern bool gSelectedCats[256];
extern bool gAnyCatSelected;

// Supported Asterix format names
const char *CAsterixFormat::_FormatName[CAsterixFormat::ETotalFormats] =
        {
                "ASTERIX_RAW",
                "ASTERIX_PCAP",
                "ASTERIX_TXT",
                "ASTERIX_FINAL",
                "ASTERIX_XML",
                "ASTERIX_XMLH",
                "ASTERIX_JSON",
                "ASTERIX_JSONH",
                "ASTERIX_JSONE",
                "ASTERIX_HDLC",
                "ASTERIX_ORADIS_RAW",
                "ASTERIX_ORADIS_PCAP",
                "ASTERIX_OUT",
                "ASTERIX_GPS"
        };

//CBaseFormatDescriptor* CAsterixFormat::m_pFormatDescriptor = NULL;


bool
CAsterixFormat::ReadPacket(CBaseFormatDescriptor &formatDescriptor, CBaseDevice &device, const unsigned int formatType,
                           bool &discard) {
    switch (formatType) {
        case ERaw:
            return CAsterixRawSubformat::ReadPacket(formatDescriptor, device, discard);
        case EPcap:
            return CAsterixPcapSubformat::ReadPacket(formatDescriptor, device, discard);
        case EOradisRaw:
            return CAsterixRawSubformat::ReadPacket(formatDescriptor, device, discard, true);
        case EOradisPcap:
            return CAsterixPcapSubformat::ReadPacket(formatDescriptor, device, discard, true);
        case EFinal:
            return CAsterixFinalSubformat::ReadPacket(formatDescriptor, device, discard);
        case EHDLC:
            return CAsterixHDLCSubformat::ReadPacket(formatDescriptor, device, discard);
        case EGPS:
            return CAsterixGPSSubformat::ReadPacket(formatDescriptor, device, discard);
        case EXML:
        case EXMLH:
        case EJSON:
        case EJSONH:
        case EJSONE:
            //todo not supported
            return false;
        default:
            ASSERT(0);
    }
    LOGERROR(1, "Unsupported format type %d.\n", formatType);
    return false;
}

bool
CAsterixFormat::WritePacket(CBaseFormatDescriptor &formatDescriptor, CBaseDevice &device, const unsigned int formatType,
                            bool &discard) {
    std::string strPacketDescription;

    switch (formatType) {
        case ERaw:
            return CAsterixRawSubformat::WritePacket(formatDescriptor, device, discard); //TODO
        case EPcap:
            return CAsterixPcapSubformat::WritePacket(formatDescriptor, device, discard);//TODO
        case EOradisRaw:
            return CAsterixRawSubformat::WritePacket(formatDescriptor, device, discard, true); //TODO
        case EOradisPcap:
            return CAsterixPcapSubformat::WritePacket(formatDescriptor, device, discard, true);//TODO
        case EFinal:
            return CAsterixFinalSubformat::WritePacket(formatDescriptor, device, discard); // TODO
        case EHDLC:
            return CAsterixHDLCSubformat::WritePacket(formatDescriptor, device, discard);//TODO
        case EGPS:
            return CAsterixGPSSubformat::WritePacket(formatDescriptor, device, discard);//TODO
        case EXML:
        case EXMLH:
        case EJSON:
        case EJSONH:
        case EJSONE:
        case ETxt:
        case EOut: {
            CAsterixFormatDescriptor &Descriptor((CAsterixFormatDescriptor &) formatDescriptor);

            if (Descriptor.m_pAsterixData == NULL) {
                LOGERROR(1, "Asterix data packet not present\n");
                return true;
            }

            if (!Descriptor.m_pAsterixData->getText(strPacketDescription, formatType)) {
                LOGERROR(1, "Failed to get data packet description\n");
                return false;
            }

            device.Write(strPacketDescription.c_str(), strPacketDescription.length());

            return true;
        }
        default:
            ASSERT(0);
    }
    LOGERROR(1, "Unsupported format type %d.\n", formatType);
    return false;
}


bool CAsterixFormat::ProcessPacket(CBaseFormatDescriptor &formatDescriptor, CBaseDevice &device,
                                   const unsigned int formatType, bool &discard) {
    discard = false;

    switch (formatType) {
        case ERaw:
            return CAsterixRawSubformat::ProcessPacket(formatDescriptor, device, discard);
        case EPcap:
            return CAsterixPcapSubformat::ProcessPacket(formatDescriptor, device, discard);
        case EOradisRaw:
            return CAsterixRawSubformat::ProcessPacket(formatDescriptor, device, discard, true);
        case EOradisPcap:
            return CAsterixPcapSubformat::ProcessPacket(formatDescriptor, device, discard, true);
        case EFinal:
            return CAsterixFinalSubformat::ProcessPacket(formatDescriptor, device, discard);
        case EHDLC:
            return CAsterixHDLCSubformat::ProcessPacket(formatDescriptor, device, discard);
        case EGPS:
            return CAsterixGPSSubformat::ProcessPacket(formatDescriptor, device, discard);
        case ETxt:
        case EXML:
        case EXMLH:
        case EJSON:
        case EJSONH:
        case EJSONE:
        case EOut:
            return false;
        default:
            ASSERT(0);
    }
    LOGERROR(1, "Unsupported format type %d.\n", formatType);
    return false;
}

bool CAsterixFormat::HeartbeatProcessing(
        CBaseFormatDescriptor &formatDescriptor,
        CBaseDevice &device,
        const unsigned int formatType) {
    switch (formatType) {
        case ERaw:
            return CAsterixRawSubformat::Heartbeat(formatDescriptor, device);
        case EPcap:
            return CAsterixPcapSubformat::Heartbeat(formatDescriptor, device);
        case EOradisRaw:
            return CAsterixRawSubformat::Heartbeat(formatDescriptor, device, true);
        case EOradisPcap:
            return CAsterixPcapSubformat::Heartbeat(formatDescriptor, device, true);
        case EFinal:
            return CAsterixFinalSubformat::Heartbeat(formatDescriptor, device);
        case EHDLC:
            return CAsterixHDLCSubformat::Heartbeat(formatDescriptor, device);
        case EGPS:
            return CAsterixGPSSubformat::Heartbeat(formatDescriptor, device);
        case ETxt:
        case EXML:
        case EXMLH:
        case EJSON:
        case EJSONH:
        case EJSONE:
        case EOut:
            return false;
        default:
            ASSERT(0);
    }
    LOGERROR(1, "Unsupported format type %d.\n", formatType);
    return false;
}

static void debug_trace(char const *format, ...) {
    char buffer[1025];
    va_list args;
    va_start (args, format);
    vsnprintf(buffer, 1024, format, args);
    va_end (args);
    strcat(buffer, "\n");
    LOGERROR(1, "%s", buffer);
}

CBaseFormatDescriptor *CAsterixFormat::CreateFormatDescriptor
        (const unsigned int formatType, const char *sFormatDescriptor) {
    if (!m_pFormatDescriptor) {
        char inputFile[256];

        Tracer::Configure(debug_trace);

        // initialize Fulliautomatix engine
        AsterixDefinition *pDefinition = new AsterixDefinition();

        // open asterix.ini file
        FILE *fpini = fopen(gAsterixDefinitionsFile, "rt");
        if (!fpini) {
            LOGERROR(1, "Failed to open definitions file");
            return NULL;
        }

        // extract ini file path
        std::string strInifile = gAsterixDefinitionsFile;
        std::string strInifilePath;
        int index = strInifile.find_last_of('\\');
        if (index < 0)
            index = strInifile.find_last_of('/');
        if (index > 0) {
            strInifilePath = strInifile.substr(0, index + 1);
        }

        bool targetFound = false;
        std::set<std::string> loadedFiles;

        while (fgets(inputFile, sizeof(inputFile), fpini)) {
            std::string strInputFile;

            // remove trailing /n from filename
            int lastChar = strlen(inputFile) - 1;
            while (lastChar >= 0 && (inputFile[lastChar] == '\r' || inputFile[lastChar] == '\n')) {
                inputFile[lastChar] = 0;
                lastChar--;
            }
            if (lastChar <= 0)
                continue;

            strInputFile = inputFile;

            // Filter by category and version if specified
            bool skip = false;

            // Only apply filters to category files (asterix_cat...)
            if (strncmp(inputFile, "asterix_cat", 11) == 0) {
                int fileCat = 0, fileMajor = 0, fileMinor = 0;
                if (sscanf(inputFile, "asterix_cat%03d_%d_%d.xml", &fileCat, &fileMajor, &fileMinor) == 3) {
                    if (fileCat >= 0 && fileCat <= 255 && gSelectedCats[fileCat]) {
                        // Category matches one requested on CLI, apply version filter
                        int reqMajor = gSelectedCatVersions[fileCat][0];
                        int reqMinor = gSelectedCatVersions[fileCat][1];

                        if (reqMajor > 0) {
                            if (fileMajor != reqMajor || (reqMinor > 0 && fileMinor != reqMinor)) {
                                skip = true; // Skip wrong versions of overridden categories
                            } else {
                                targetFound = true;
                            }
                        } else {
                            // Category matches and no specific version requested, accept it
                            targetFound = true;
                        }
                    } else {
                        // Not an overridden category, load normally from .ini
                        skip = false;
                    }
                } else {
                    // Filename doesn't match pattern, skip it
                    skip = true;
                }
            } else {
                // Non-category files (like asterix_bds.xml) are always loaded
                skip = false;
            }

            if (skip) {
                continue;
            }

            // Deduplicate: don't load the same file twice
            if (loadedFiles.find(strInputFile) != loadedFiles.end()) {
                continue;
            }

            FILE *fp = fopen(strInputFile.c_str(), "rt");
            if (!fp) {
                // try in folder where is ini file
                if (!strInifilePath.empty()) {
                    std::string strfilepath = strInifilePath + strInputFile;
                    fp = fopen(strfilepath.c_str(), "rt");
                }

                if (!fp) {
                    LOGERROR(1, "Failed to open definitions file: %s\n", inputFile);
                    continue;
                }
            }

            // parse format file
            XMLParser Parser;
            if (!Parser.Parse(fp, pDefinition, inputFile)) {
                LOGERROR(1, "Failed to parse definitions file: %s\n", strInputFile.c_str());
            } else {
                loadedFiles.insert(strInputFile);
            }

            fclose(fp);
        }

        // Dynamic Loading Fallback: For each requested cat/version, if NOT found in .ini, try to load it directly
        if (gAnyCatSelected) {
            for (int cat = 0; cat < 256; cat++) {
                if (gSelectedCats[cat]) {
                    int reqMajor = gSelectedCatVersions[cat][0];
                    int reqMinor = gSelectedCatVersions[cat][1];

                    // We only dynamically load if a specific version was requested for this category
                    // and it wasn't already loaded (either from .ini or previous cat loop)
                    if (reqMajor > 0) {
                        char dynamicFile[256];
                        snprintf(dynamicFile, sizeof(dynamicFile), "asterix_cat%03d_%d_%d.xml", cat, reqMajor,
                                 reqMinor);
                        std::string strDynamicFile = dynamicFile;

                        if (loadedFiles.find(strDynamicFile) == loadedFiles.end()) {
                            FILE *fp = fopen(strDynamicFile.c_str(), "rt");
                            if (!fp && !strInifilePath.empty()) {
                                std::string strfilepath = strInifilePath + strDynamicFile;
                                fp = fopen(strfilepath.c_str(), "rt");
                            }

                            if (fp) {
                                XMLParser Parser;
                                if (!Parser.Parse(fp, pDefinition, dynamicFile)) {
                                    LOGERROR(1, "Failed to parse dynamic definitions file: %s\n", dynamicFile);
                                } else {
                                    loadedFiles.insert(strDynamicFile);
                                    LOGINFO(1, "Dynamically loaded requested ASTERIX version: %s\n", dynamicFile);
                                }
                                fclose(fp);
                            } else {
                                // Only warn if it's a specific version we expected to find
                                LOGWARNING(1, "Requested ASTERIX category %d version %d.%d file not found: %s\n", cat,
                                           reqMajor, reqMinor, dynamicFile);
                            }
                        }
                    }
                }
            }
        }

        fclose(fpini);

        m_pFormatDescriptor = new CAsterixFormatDescriptor(pDefinition);
    }
    return m_pFormatDescriptor;
}


bool CAsterixFormat::GetFormatNo(const char *formatName, unsigned int &formatType) {
    bool found = false;
    unsigned int i;

    for (i = 0; (i < ETotalFormats) && (!found); i++) {
        ASSERT(formatName);
        if (strcasecmp(formatName, CAsterixFormat::_FormatName[i]) == 0) {
            found = true;
            formatType = i;
        }
    }

    return found;
}

int CAsterixFormat::GetStatus(CBaseDevice &device, const unsigned int formatType, int query) {
    if (device.IsOpened() == false)
        return STS_NO_DATA;

    return 0;
}


bool CAsterixFormat::OnResetInputChannel(CBaseFormatDescriptor &formatDescriptor) {
    return false;
}


bool CAsterixFormat::OnResetOutputChannel(unsigned int channel, CBaseFormatDescriptor &formatDescriptor) {
    return true;
}




