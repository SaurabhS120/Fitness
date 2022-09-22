package com.example.fitness.domain

interface LocalRepo {
    fun isFirstLaunch(): Boolean
}