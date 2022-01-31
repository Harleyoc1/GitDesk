package com.harleyoconnor.gitdesk.data.remote.timeline

import com.harleyoconnor.gitdesk.data.remote.Label

interface LabeledEvent : Event {

    val label: Label

}