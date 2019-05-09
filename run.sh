#!/bin/bash
SERVICE_DIR=service
git submodule update --init
for ser in ${SERVICE_DIR}/*
do
	cd $ser
	gradle clean bootRun &
	cd ../..
done
