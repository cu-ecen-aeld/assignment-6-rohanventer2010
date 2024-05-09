# See https://git.yoctoproject.org/poky/tree/meta/files/common-licenses
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

# Set this  with the path to your assignments rep.  Use ssh protocol and see lecture notes
# about how to setup ssh-agent for passwordless access
FILESEXTRAPATHS:prepend := "${THISDIR}/:"
SRC_URI = "git://git@github.com/cu-ecen-aeld/assignment-7-rohanventer2010.git;protocol=ssh;branch=master"
SRC_URI += "file://misc_init"

PV = "1.0+git${SRCPV}"
# set to reference a specific commit hash in your assignment repo
SRCREV = "eaa3570b5fbf32e82668cc931d109660aa35f82e"

# This sets your staging directory based on WORKDIR, where WORKDIR is defined at 
# https://docs.yoctoproject.org/ref-manual/variables.html?highlight=workdir#term-WORKDIR
# We reference the "server" directory here to build from the "server" directory
# in your assignments repo
S = "${WORKDIR}/git/misc-modules"

# Ensure your recipe uses inherit module and ensure this is placed at the top of your recipe and before any FILES_${PN}.
inherit module
EXTRA_OEMAKE:append:task-install = " -C ${STAGING_KERNEL_DIR} M=${S}/misc-modules"
EXTRA_OEMAKE += "KERNELDIR=${STAGING_KERNEL_DIR}"

# See https://git.yoctoproject.org/poky/plain/meta/conf/bitbake.conf?h=kirkstone
FILES:${PN} += "${sysconfdir}/*"
FILES:${PN} += "${bindir}/*"

inherit update-rc.d 
INITSCRIPT_PACKAGES = "${PN}"
INITSCRIPT_NAME:${PN} = "misc_init"
INITSCRIPT_PARAMS = "defaults 90 10"

do_configure () {
	:
}

do_compile () {
	oe_runmake
}

do_install () {
	# Install your binaries/scripts here.
	# Be sure to install the target directory with install -d first
	# Yocto variables ${D} and ${S} are useful here, which you can read about at 
	# https://docs.yoctoproject.org/ref-manual/variables.html?highlight=workdir#term-D
	# and
	# https://docs.yoctoproject.org/ref-manual/variables.html?highlight=workdir#term-S
	# See example at https://github.com/cu-ecen-aeld/ecen5013-yocto/blob/ecen5013-hello-world/meta-ecen5013/recipes-ecen5013/ecen5013-hello-world/ecen5013-hello-world_git.bb
	install -d ${D}${base_libdir}/modules/${KERNEL_VERSION}/extra
	install -m 0755 ${S}/faulty.ko ${D}${base_libdir}/modules/${KERNEL_VERSION}/extra/
	install -m 0755 ${S}/hello.ko ${D}${base_libdir}/modules/${KERNEL_VERSION}/extra/

	install -d ${D}${bindir}
	install -m 0755 ${S}/module_load ${D}${bindir}/
	install -m 0755 ${S}/module_unload ${D}${bindir}/

	install -d ${D}${sysconfdir}/init.d
	install -m 0755 ${WORKDIR}/misc_init ${D}${sysconfdir}/init.d/	
}
