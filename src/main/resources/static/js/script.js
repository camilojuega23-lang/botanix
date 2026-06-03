const searchInput = document.getElementById("searchInput");
const table = document.querySelector("#tablaUsuarios, #tablaPlantas, #tablaJardinero");

if (searchInput && table) {
    searchInput.addEventListener("keyup", function () {

        let filtro = searchInput.value.toLowerCase();

        let filas = table.querySelectorAll("tbody tr");

        filas.forEach(fila => {

            let texto = fila.textContent.toLowerCase();

            fila.style.display = texto.includes(filtro)
                ? ""
                : "none";

        });

    });
}
