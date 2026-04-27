class SearchTable {

    constructor(config) {
        this.input = document.getElementById(config.inputId);
        this.tableBody = document.getElementById(config.tableBodyId);
        this.loader = document.getElementById(config.loaderId);
        this.pagination = document.getElementById(config.paginationId || 'paginationContainer');

        this.apiUrl = config.apiUrl;
        this.renderRow = config.renderRow;
        this.pageSize = config.pageSize || 10;

        this.currentPage = 0;
        this.currentKeyword = "";
        this.timeout = null;

        this.init();
    }

    init() {
        if (this.input) {
            // Bloque ENTER pour ne pas recharger
            this.input.addEventListener("keydown", (e) => {
                if (e.key === "Enter") e.preventDefault();
            });

            // Déclenche recherche avec debounce
            this.input.addEventListener("input", () => {
                clearTimeout(this.timeout);
                this.timeout = setTimeout(() => {
                    this.currentKeyword = this.input.value;
                    this.load(0);
                }, 400);
            });
        }

        this.load(0);
    }

    async load(page = 0) {
        this.currentPage = page;
        this.showLoader();

        try {
            const url = `${this.apiUrl}?keyword=${encodeURIComponent(this.currentKeyword)}&page=${page}&size=${this.pageSize}`;
            const res = await fetch(url);
            if (!res.ok) throw new Error("Erreur serveur");

            const data = await res.json();
            this.render(data);

        } catch (e) {
            console.error("Erreur chargement :", e);
            this.tableBody.innerHTML = `
                <tr>
                    <td colspan="100%" class="text-danger text-center">
                        Erreur de chargement
                    </td>
                </tr>`;
        } finally {
            this.hideLoader();
        }
    }

    render(data) {
        // Corps du tableau
        if (!data.content || data.content.length === 0) {
            this.tableBody.innerHTML = `<tr><td colspan="100%" class="text-center">Aucun résultat</td></tr>`;
            this.pagination.innerHTML = '';
            return;
        }

        this.tableBody.innerHTML = data.content.map(p => this.renderRow(p)).join('');

        this.attachDeleteEvents();
        this.buildPagination(data);
    }

    attachDeleteEvents() {
        document.querySelectorAll(".del-btn").forEach(btn => {
            btn.onclick = (e) => {
                e.preventDefault();
                const href = btn.href;

                Swal.fire({
                    title: 'Confirmation',
                    text: "Supprimer cet élément ?",
                    icon: 'warning',
                    showCancelButton: true,
                    confirmButtonText: "Oui",
                    cancelButtonText: "Annuler"
                }).then(r => {
                    if (r.isConfirmed) {
                        fetch(href, { method: 'DELETE' })
                            .then(() => this.load(this.currentPage))
                            .catch(err => console.error(err));
                    }
                });
            };
        });
    }

    buildPagination(data) {
        if (!this.pagination) return;

        const totalPages = data.totalPages;
        const current = data.number;
        if (totalPages <= 1) {
            this.pagination.innerHTML = '';
            return;
        }

        let pages = '';

        for (let i = 0; i < totalPages; i++) {
            pages += `
                <li class="page-item ${i === current ? 'active' : ''}">
                    <a href="#" class="page-link" data-page="${i}">${i + 1}</a>
                </li>`;
        }

        this.pagination.innerHTML = pages;

        this.pagination.querySelectorAll(".page-link").forEach(link => {
            link.onclick = (e) => {
                e.preventDefault();
                this.load(parseInt(link.dataset.page));
            };
        });
    }

    showLoader() { if (this.loader) this.loader.classList.remove("d-none"); }
    hideLoader() { if (this.loader) this.loader.classList.add("d-none"); }

}