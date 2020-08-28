#!/bin/bash
set -e

tag() {
    THIS_TAG=$(git rev-list --count HEAD)

    git config --global user.email "jorge.diazbenitosoriano@gmail.com"
    git config --global user.name "Jorge Antonio Diaz-Benito Soriano"

    git tag -a $THIS_TAG -m $THIS_TAG

    git push --tags
}

tag
