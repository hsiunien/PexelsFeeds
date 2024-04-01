package wang.xiunian.pexelsdemo.ui.main

import wang.xiunian.pexelsdemo.ui.main.entity.PhotosResponse

sealed class EventMessage {
    class ItemClickEvent(val itemData: PhotosResponse) : EventMessage()
}