# Asakusa Framework Tutorial

Basic tutorial documentation for [Asakusa Framework](https://github.com/asakusafw/asakusafw).

**Sorry, this documentation is currently Japanese only.**

## How to Build

This tutorial uses the [Sphinx](https://www.sphinx-doc.org) documentation system.

Building the documentation requires at least version 1.4 of [Sphinx](https://www.sphinx-doc.org) and the [Sphinx RTD Theme](https://pypi.python.org/pypi/sphinx_rtd_theme), [pygments-dmdl](https://pypi.python.org/pypi/pygments-dmdl) have to be installed.

```
pip install sphinx
pip install sphinx-rtd-theme
pip install pygments-dmdl
```

After installing:

```
cd docs/ja
make html
```

Then, open your browser to ``build/html/index.html``.

## Patch contribution
* Please contribute with patches according to our [contribution guide (Japanese only, English version to be added)](https://docs.asakusafw.com/latest/release/ja/html/contribution.html)

## License
* [Apache License, Version 2.0](http://www.apache.org/licenses/LICENSE-2.0)