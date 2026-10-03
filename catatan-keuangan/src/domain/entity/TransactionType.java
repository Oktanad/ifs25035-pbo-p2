package domain.entity;

/**
 * Jenis transaksi keuangan.
 * Urutan deklarasi dipakai sebagai urutan alami: INCOME lebih dulu dari EXPENSE.
 */
public enum TransactionType {
    /** Uang masuk (menambah saldo). */
    INCOME,

    /** Uang keluar (mengurangi saldo). */
    EXPENSE
}