package framework.view;

import adapter.presenter.FinancePresenter;
import domain.entity.SortOption;
import framework.util.InputUtil;
import usecase.FinanceUseCase;

/**
 * View (CLI) aplikasi catatan keuangan.
 * Menampilkan menu, membaca input user, dan mendelegasikan
 * logika bisnis ke use case serta format output ke presenter.
 */
public class FinanceView {
    private final FinanceUseCase financeUseCase;
    private final FinancePresenter presenter;

    public FinanceView(FinanceUseCase financeUseCase, FinancePresenter presenter) {
        this.financeUseCase = financeUseCase;
        this.presenter = presenter;
    }

    /** Menampilkan menu utama dan loop interaksi user. */
    public void show() {
        boolean running = true;
        while (running) {
            presenter.showTransactions(
                    financeUseCase.getAllTransactions(),
                    financeUseCase.getBalance());
            printMenu();
            String choice = InputUtil.input("Pilih");
            switch (choice) {
                case "1" -> addIncome();
                case "2" -> addExpense();
                case "3" -> searchTransaction();
                case "4" -> sortTransaction();
                case "5" -> showBalance();
                case "6" -> removeTransaction();
                case "x" -> running = false;
                default -> presenter.showInvalidChoice();
            }
            if (running) {
                System.out.println();
            }
        }
    }

    private void printMenu() {
        System.out.println("Menu:");
        System.out.println("1. Tambah Pemasukan");
        System.out.println("2. Tambah Pengeluaran");
        System.out.println("3. Cari");
        System.out.println("4. Urutkan");
        System.out.println("5. Lihat Saldo");
        System.out.println("6. Hapus");
        System.out.println("x. Keluar");
    }

    /** Form tambah pemasukan. */
    private void addIncome() {
        System.out.println("[Tambah Pemasukan]");
        String description = InputUtil.input("Keterangan (x Jika Batal)");
        if (description.equals("x")) {
            return;
        }

        Long amount = readAmount();
        if (amount == null) {
            return;
        }

        presenter.showAddSuccess(financeUseCase.addIncome(description, amount));
    }

    /** Form tambah pengeluaran. */
    private void addExpense() {
        System.out.println("[Tambah Pengeluaran]");
        String description = InputUtil.input("Keterangan (x Jika Batal)");
        if (description.equals("x")) {
            return;
        }

        Long amount = readAmount();
        if (amount == null) {
            return;
        }

        presenter.showAddSuccess(financeUseCase.addExpense(description, amount));
    }

    /** Form pencarian transaksi berdasarkan keterangan. */
    private void searchTransaction() {
        System.out.println("[Cari Transaksi]");
        String keyword = InputUtil.input("Kata Kunci (x Jika Batal)");
        if (keyword.equals("x")) {
            return;
        }
        presenter.showSearchResults(financeUseCase.searchTransactions(keyword), keyword);
    }

    /** Form pengurutan transaksi. */
    private void sortTransaction() {
        System.out.println("[Urutkan Transaksi]");
        System.out.println("1. Jumlah (Terkecil)");
        System.out.println("2. Jumlah (Terbesar)");
        System.out.println("3. Pemasukan Dulu");
        System.out.println("4. Pengeluaran Dulu");
        System.out.println("x. Batal");
        String choice = InputUtil.input("Pilih");
        if (choice.equals("x")) {
            return;
        }
        SortOption option = mapSortOption(choice);
        if (option == null) {
            presenter.showInvalidSortOption();
            return;
        }
        presenter.showSortedTransactions(
                financeUseCase.sortTransactions(option),
                financeUseCase.getBalance());
    }

    /** Menampilkan saldo saat ini. */
    private void showBalance() {
        presenter.showBalance(financeUseCase.getBalance());
    }

    /** Form hapus transaksi. */
    private void removeTransaction() {
        System.out.println("[Hapus Transaksi]");
        String input = InputUtil.input("ID Transaksi (x Jika Batal)");
        if (input.equals("x")) {
            return;
        }
        Integer id = parseId(input);
        if (id == null) {
            return;
        }
        if (financeUseCase.removeTransaction(id)) {
            presenter.showRemoveSuccess();
        } else {
            presenter.showRemoveFailed(id);
        }
    }

    /** Membaca dan memvalidasi jumlah (> 0, numerik). */
    private Long readAmount() {
        String input = InputUtil.input("Jumlah");
        try {
            long amount = Long.parseLong(input.trim());
            if (amount <= 0) {
                presenter.showInvalidAmount();
                return null;
            }
            return amount;
        } catch (NumberFormatException e) {
            presenter.showInvalidAmount();
            return null;
        }
    }

    /** Parse ID; tampilkan error jika bukan angka. */
    private Integer parseId(String input) {
        try {
            return Integer.parseInt(input.trim());
        } catch (NumberFormatException e) {
            presenter.showInvalidId();
            return null;
        }
    }

    /** Memetakan pilihan menu (1-4) ke {@link SortOption} domain. */
    private SortOption mapSortOption(String choice) {
        return switch (choice) {
            case "1" -> SortOption.AMOUNT_ASC;
            case "2" -> SortOption.AMOUNT_DESC;
            case "3" -> SortOption.INCOME_FIRST;
            case "4" -> SortOption.EXPENSE_FIRST;
            default -> null;
        };
    }
}
