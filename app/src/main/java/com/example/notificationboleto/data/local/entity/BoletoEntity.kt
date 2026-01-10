package com.example.notificationboleto.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "boletos")
data class BoletoEntity(
    @PrimaryKey val id: String,
    val nome: String,
    val valor: String,
    val vencimento: String,
    val descricao: String
)
