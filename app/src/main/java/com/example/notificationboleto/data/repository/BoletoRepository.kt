package com.example.notificationboleto.data.repository

import com.example.notificationboleto.data.local.dao.BoletoDao
import com.example.notificationboleto.data.local.entity.BoletoEntity
import kotlinx.coroutines.flow.Flow

class BoletoRepository(private val dao: BoletoDao) {

    fun getBoletos(): Flow<List<BoletoEntity>> = dao.getBoletos()

    suspend fun getAllBoletos(): List<BoletoEntity> = dao.getAllBoletos()

    suspend fun addBoleto(boleto: BoletoEntity) = dao.insert(boleto)

    suspend fun updateBoleto(boleto: BoletoEntity) = dao.update(boleto)

    suspend fun deleteBoleto(boleto: BoletoEntity) = dao.delete(boleto)
}
