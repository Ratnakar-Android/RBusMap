package com.redbus.map.module

import java.lang.Exception

class MapExceptions(exceptionDetails: String): Exception(exceptionDetails){

    companion object{
        val invalidSelectedPath = "Please select valid path to save"
        val insufficientPathDataToSave = "Please select valid source destination and path"
    }
}