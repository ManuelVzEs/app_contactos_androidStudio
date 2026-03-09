package com.example.app_contactos

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, "contactos.db", null, 1) {

    companion object {
        const val TABLE = "contactos"
        const val COL_ID = "id"
        const val COL_NOMBRE = "nombre"
        const val COL_TELEFONO = "telefono"
        const val COL_EMAIL = "email"
        const val COL_FOTO = "foto"
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(
            """CREATE TABLE $TABLE (
                $COL_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COL_NOMBRE TEXT NOT NULL,
                $COL_TELEFONO TEXT,
                $COL_EMAIL TEXT,
                $COL_FOTO TEXT
            )"""
        )
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE")
        onCreate(db)
    }

    fun insertarContacto(contacto: Contacto): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COL_NOMBRE, contacto.nombre)
            put(COL_TELEFONO, contacto.telefono)
            put(COL_EMAIL, contacto.email)
            put(COL_FOTO, contacto.fotoUri)
        }
        val id = db.insert(TABLE, null, values)
        db.close()
        return id
    }

    fun obtenerTodos(): List<Contacto> {
        val lista = mutableListOf<Contacto>()
        val db = readableDatabase
        val cursor = db.query(TABLE, null, null, null, null, null, "$COL_NOMBRE ASC")
        while (cursor.moveToNext()) {
            lista.add(
                Contacto(
                    id       = cursor.getInt(cursor.getColumnIndexOrThrow(COL_ID)),
                    nombre   = cursor.getString(cursor.getColumnIndexOrThrow(COL_NOMBRE)) ?: "",
                    telefono = cursor.getString(cursor.getColumnIndexOrThrow(COL_TELEFONO)) ?: "",
                    email    = cursor.getString(cursor.getColumnIndexOrThrow(COL_EMAIL)) ?: "",
                    fotoUri  = cursor.getString(cursor.getColumnIndexOrThrow(COL_FOTO)) ?: ""
                )
            )
        }
        cursor.close()
        db.close()
        return lista
    }

    fun obtenerPorId(id: Int): Contacto? {
        val db = readableDatabase
        val cursor = db.query(TABLE, null, "$COL_ID=?", arrayOf(id.toString()), null, null, null)
        var contacto: Contacto? = null
        if (cursor.moveToFirst()) {
            contacto = Contacto(
                id       = cursor.getInt(cursor.getColumnIndexOrThrow(COL_ID)),
                nombre   = cursor.getString(cursor.getColumnIndexOrThrow(COL_NOMBRE)) ?: "",
                telefono = cursor.getString(cursor.getColumnIndexOrThrow(COL_TELEFONO)) ?: "",
                email    = cursor.getString(cursor.getColumnIndexOrThrow(COL_EMAIL)) ?: "",
                fotoUri  = cursor.getString(cursor.getColumnIndexOrThrow(COL_FOTO)) ?: ""
            )
        }
        cursor.close()
        db.close()
        return contacto
    }

    fun actualizarContacto(contacto: Contacto): Int {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COL_NOMBRE, contacto.nombre)
            put(COL_TELEFONO, contacto.telefono)
            put(COL_EMAIL, contacto.email)
            put(COL_FOTO, contacto.fotoUri)
        }
        val rows = db.update(TABLE, values, "$COL_ID=?", arrayOf(contacto.id.toString()))
        db.close()
        return rows
    }

    fun eliminarContacto(id: Int) {
        val db = writableDatabase
        db.delete(TABLE, "$COL_ID=?", arrayOf(id.toString()))
        db.close()
    }
}