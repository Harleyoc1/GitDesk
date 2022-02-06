package com.harleyoconnor.gitdesk.util

private fun property(key: String) = System.getProperty(key)

fun getOsName(): String = property("os.name")
fun getUserName(): String = property("user.name")
fun getUserHome(): String = property("user.home")
