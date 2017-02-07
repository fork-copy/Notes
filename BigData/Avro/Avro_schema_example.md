
复杂类型

```
{
    "name" : "five",
    "type":"record",
    "fields": [
         {"name": "schoolID", "type": "string"},
         {"name": "sysID", "type": "string"},
         {"name": "schoolName", "type": "string"},
         {"name": "lstStuFeature", "type":
             {"type": "array",
                "items":{
                    "type":"record",
                    "name" : "feature",
                    "fields":[
                      {"name":"studentID","type":"string"},
                      {"name":"studentName","type":"string"},
                      {"name":"gradeLength","type":"string"},
                      {"name":"meterialTypeOn","type":"string"},
                      {"name":"meterialActionOn","type":"string"},
                      {"name":"studyTimeOn","type":"string"},
                      {"name":"optionOn","type":"string"},
                      {"name":"controlOn","type":"string"}
                    ]}
             }
         },
         {"name":"lstStuBehavior","type":{
           "type": "array",
           "items":{
                "type":"record",
                "name":"behavior",
                "fields":[
                  {"name":"studentID","type":"string"},
                  {"name":"studentName","type":"string"},
                  {"name":"gradeLength","type":"string"},
                  {"name":"platFormUse_Count","type":"int"},
                  {"name":"platFormUse_Time","type":"int"},
                  {"name":"resourceLearn_Count","type":"int"},
                  {"name":"interactCount","type":"int"},
                  {"name":"lessonOnTime","type":"int"},
                  {"name":"lessonLate","type":"int"},
                  {"name":"lessonAbsent","type":"int"},
                  {"name":"homeworkOnTime","type":"int"},
                  {"name":"homeworkLate","type":"int"},
                  {"name":"lessonSpeakCount","type":"int"}
                ]}
           }
         },
         {"name":"lstTeaBehavior","type":{
           "type": "array",
           "items":{
                 "type":"record",
                 "name":"teaBehavior",
                 "fields":[
                   {"name":"teacherID","type":"string"},
                   {"name":"teacherName","type":"string"},
                   {"name":"lessonCount","type":"int"},
                   {"name":"hwPublishCount","type":"int"},
                   {"name":"platFormUse_Count","type":"int"},
                   {"name":"platFormUse_Time","type":"int"},
                   {"name":"lsDataCount_Kj","type":"int"},
                   {"name":"lsDataCount_Zl","type":"int"},
                   {"name":"lsDataCount_Wp","type":"int"},
                   {"name":"lsDataCount_Jc","type":"int"},
                   {"name":"lsDataCount_Yl","type":"int"},
                   {"name":"lsDataCount_Zy","type":"int"},
                   {"name":"lsDataCount_Pc","type":"int"},
                   {"name":"lsDataCount_U","type":"int"},
                   {"name":"lsDataCount_Sys","type":"int"},
                   {"name":"resourceUploadCount","type":"int"},
                   {"name":"lessonHour_Teach","type":"int"},
                   {"name":"lessonHour_Test","type":"int"},
                   {"name":"lessonHour_SelfStudy","type":"int"}
                 ]}
           }
         },
         {
            "name":"teachResource", "type":{
                 "type": "record",
                 "name": "resource",
                 "fields":[
                       {"name":"lstRLibrary",
                       "type":{
                            "type": "array",
                            "items":{
                                  "type":"record",
                                  "name":"rLibrary",
                                  "fields":[
                                    {"name":"rlibraryID","type":"string"},
                                    {"name":"rlibraryName","type":"string"},
                                    {"name":"size","type":"int"},
                                    {"name":"documentCount","type":"int"},
                                    {"name":"audioCount","type":"int"},
                                    {"name":"videoCount","type":"int"},
                                    {"name":"pictureCount","type":"int"},
                                    {"name":"pageCount","type":"int"},
                                    {"name":"courwareCount","type":"int"},
                                    {"name":"otherCount","type":"int"}
                                  ]}
                            }
                       },
                       {"name":"lstTheResource",
                       "type":{
                           "type": "array",
                           "items":{
                                 "type":"record",
                                 "name":"theResource",
                                 "fields":[
                                   {"name":"rlibraryID","type":"string"},
                                   {"name":"rlibraryName","type":"string"},
                                   {"name":"resourceID","type":"string"},
                                   {"name":"resourceName","type":"string"},
                                   {"name":"authorID","type":"string"},
                                   {"name":"authorName","type":"string"},
                                   {"name":"ranking","type":"int"},
                                   {"name":"upateTime","type":"long"}
                                 ]}
                           }
                       }
                ]
            }
         },
         {"name":"lstTeachEquipment","type":{
                    "type": "array",
                    "items":{
                          "type":"record",
                          "name":"teachEquipment",
                          "fields":[
                            {"name":"roomID","type":"string"},
                            {"name":"roomName","type":"string"},
                            {"name":"roomType","type":"int"},
                            {"name":"lstTheEquipment","type":{
                                       "type": "array",
                                       "items":{
                                            "type":"record",
                                            "name":"theEquipment",
                                            "fields":[
                                              {"name":"equipmentID","type":"string"},
                                              {"name":"equipmentName","type":"string"},
                                              {"name":"modelNum","type":"string"},
                                              {"name":"state","type":"int"},
                                              {"name":"workDuration","type":"int"}
                                            ]}
                                       }
                                     }
                          ]}
                    }
                  }
    ]
}
```